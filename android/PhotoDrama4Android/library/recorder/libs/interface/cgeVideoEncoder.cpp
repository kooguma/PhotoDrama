/*
* cgeVideoEncoder.cpp
*
*  Created on: 2015-7-30
*      Author: Wang Yang
*        Mail: admin@wysaid.org
*/

#ifdef _CGE_USE_FFMPEG_

#include <cassert>
#include "cgeGLFunctions.h"
#include "cgeVideoEncoder.h"

static int64_t pts = 0;

static AVStream *addStream(AVFormatContext *oc, AVCodec **codec,
                           enum AVCodecID codec_id, int frameRate, int width = -1, int height = -1,
                           int bitRate = 1650000) {
    AVCodecContext *c;
    AVStream *st;

    /* find the encoder */
    *codec = avcodec_find_encoder(codec_id);
    if (!(*codec)) {
        CGE_LOG_ERROR("Could not find encoder for '%s'\n", avcodec_get_name(codec_id));
        return nullptr;
    }

    st = avformat_new_stream(oc, *codec);
    if (!st) {
        CGE_LOG_ERROR("Could not allocate stream\n");
        return nullptr;
    }
    st->id = oc->nb_streams - 1;
    c = st->codec;

    switch ((*codec)->type) {
        case AVMEDIA_TYPE_AUDIO:
            c->sample_fmt = AV_SAMPLE_FMT_FLTP;
            c->bit_rate = 64000;
            c->sample_rate = 44100;
            c->channels = 1;
            c->flags |= CODEC_FLAG_GLOBAL_HEADER;
            c->strict_std_compliance = -2;
            break;

        case AVMEDIA_TYPE_VIDEO:
            c->codec_id = codec_id;

            c->bit_rate = bitRate;
            /* Resolution must be a multiple of two. */
            c->width = width;
            c->height = height;
            /* timebase: This is the fundamental unit of time (in seconds) in terms
             * of which frame timestamps are represented. For fixed-fps content,
             * timebase should be 1/framerate and timestamp increments should be
             * identical to 1. */
            c->time_base.den = frameRate;
            c->time_base.num = 1;
            c->gop_size = 12; /* emit one intra frame every twelve frames at most */
            c->pix_fmt = AV_PIX_FMT_YUV420P;

            av_opt_set(c->priv_data, "preset", "veryfast", 0);
            if (c->codec_id == AV_CODEC_ID_MPEG2VIDEO) {
                /* just for testing, we also add B frames */
                c->max_b_frames = 2;
            }
            if (c->codec_id == AV_CODEC_ID_MPEG1VIDEO) {
                /* Needed to avoid using macroblocks in which some coeffs overflow.
                 * This does not happen with normal video, it just happens here as
                 * the motion of the chroma plane does not match the luma plane. */
                c->mb_decision = 2;
            }
            break;

        default:
            break;
    }

    /* Some formats want stream headers to be separate. */
    if (oc->oformat->flags & AVFMT_GLOBALHEADER)
        c->flags |= CODEC_FLAG_GLOBAL_HEADER;

    return st;
}

//////////////////////////////////////////////////////////////////////////

namespace CGE {
    struct CGEEncoderContextMP4 {
        CGEEncoderContextMP4() : pOutputFmt(nullptr), pFormatCtx(nullptr), iFormatCtx(nullptr),
                                 pVideoStream(nullptr), pAudioStream(nullptr), pVideoCodec(nullptr),
                                 pAudioCodec(nullptr), pVideoFrame(nullptr), pAudioFrame(nullptr),
                                 pSwsCtx(nullptr), pSwrCtx(nullptr), dstSampleData(nullptr),
                                 dstSamplesSize(0), dstSampleDataIndex(0) {
            memset(&videoPacket, 0, sizeof(videoPacket));
            memset(&audioPacket, 0, sizeof(audioPacket));
            memset(&dstPicture, 0, sizeof(dstPicture));
        }

        ~CGEEncoderContextMP4() {
            cleanup();
        }

        inline void cleanup() {
            if (pVideoStream) {
                avcodec_close(pVideoStream->codec);
                pVideoStream = nullptr;
            }

            if (pVideoFrame) {
                av_free(dstPicture.data[0]);
                av_free(pVideoFrame);
                pVideoFrame = nullptr;
            }

            if (pAudioFrame) {
                av_frame_free(&pAudioFrame);
                pAudioFrame = nullptr;
            }

            if (pAudioStream) {
                avcodec_close(pAudioStream->codec);
                pAudioStream = nullptr;
            }

            if (pSwrCtx != nullptr) {
                av_free(dstSampleData[0]);
                dstSampleData = nullptr;
                dstSamplesLinesize = 0;
                dstSamplesSize = 0;
                swr_free(&pSwrCtx);
                pSwrCtx = nullptr;
            }

            if (pOutputFmt && pFormatCtx && !(pOutputFmt->flags & AVFMT_NOFILE)) {
                avio_close(pFormatCtx->pb);
            }

            if (pFormatCtx) {
                avformat_free_context(pFormatCtx);
                pFormatCtx = nullptr;
            }

            if (iFormatCtx) {
                avformat_free_context(iFormatCtx);
                iFormatCtx = nullptr;
            }

            pOutputFmt = nullptr;

//            av_free_packet(&audioPacket);

            memset(&videoPacket, 0, sizeof(videoPacket));
            memset(&audioPacket, 0, sizeof(audioPacket));
            memset(&dstPicture, 0, sizeof(dstPicture));

            if (pSwsCtx != nullptr) {
                sws_freeContext(pSwsCtx);
                pSwsCtx = nullptr;
            }

            pVideoCodec = nullptr;
            pAudioCodec = nullptr;
            dstSampleDataIndex = 0;
        }

        AVOutputFormat *pOutputFmt;
        AVFormatContext *pFormatCtx;
        AVFormatContext *iFormatCtx;
        AVCodecContext *mpAudioSrcCodecCxt;
        AVStream *pVideoStream;
        AVStream *pAudioStream;

        AVCodec *pVideoCodec;
        AVCodec *pAudioCodec;

        AVFrame *pVideoFrame;
        AVFrame *pAudioFrame;
        SwsContext *pSwsCtx;
        SwrContext *pSwrCtx;

        AVAudioFifo *fifo = NULL;

        AVPacket videoPacket;
        AVPacket audioPacket;

        AVPicture dstPicture;
        uint8_t **dstSampleData;
        int dstSampleDataIndex;
        int dstSamplesLinesize;
        int dstSamplesSize;

        int maxDstNbSamples;
        char* filename;

    };

    CGEVideoEncoderMP4::CGEVideoEncoderMP4() : m_videoPacketBuffer(nullptr),
                                               m_audioPacketBuffer(nullptr) {
        m_context = new CGEEncoderContextMP4;
    }

    CGEVideoEncoderMP4::~CGEVideoEncoderMP4() {
        drop();
        delete m_context;

        if (m_videoPacketBuffer != nullptr)
            av_free(m_videoPacketBuffer);

        if (m_audioPacketBuffer != nullptr)
            av_free(m_audioPacketBuffer);
    }

    bool CGEVideoEncoderMP4::init(const char *filename, int fps, int width, int height,
                                  bool hasAudio, int bitRate) {
        m_hasAudio = hasAudio;

        avformat_alloc_output_context2(&m_context->pFormatCtx, nullptr, nullptr, filename);
        if (!m_context->pFormatCtx) {
            //CGE_LOG_INFO("Could not deduce output format from file extension: using MPEG.\n");
            avformat_alloc_output_context2(&m_context->pFormatCtx, NULL, "mp4", filename);
        }

        m_filename = filename;

        // auto ret = avformat_alloc_output_context2(&m_context->pFormatCtx, nullptr, nullptr, filename);

        // CGE_LOG_ERROR("ret num %x, filename %s", ret, filename);

        if (!m_context->pFormatCtx) {
            //CGE_LOG_ERROR("avformat_alloc_output_context2 failed...");
            return false;
        }

        m_context->pOutputFmt = m_context->pFormatCtx->oformat;
        m_context->pVideoStream = nullptr;

        if (m_context->pOutputFmt->video_codec != AV_CODEC_ID_NONE) {
            m_context->pVideoStream = addStream(m_context->pFormatCtx, &m_context->pVideoCodec,
                                                m_context->pOutputFmt->video_codec, fps, width,
                                                height, bitRate);
        }

        if (m_hasAudio && m_context->pOutputFmt->audio_codec != AV_CODEC_ID_NONE) {
            m_context->pAudioStream = addStream(m_context->pFormatCtx, &m_context->pAudioCodec,
                                                m_context->pOutputFmt->audio_codec, fps, width,
                                                height, bitRate);
        }


        CGE_LOG_ERROR("m_context->pAudioStream->index %d", m_context->pAudioStream->index);

        CGE_LOG_ERROR("m_context->pVideoStream->index", m_context->pVideoStream->index);

        if (m_videoPacketBuffer != nullptr)
            av_free(m_videoPacketBuffer);

        if (m_audioPacketBuffer != nullptr) {
            av_free(m_audioPacketBuffer);
            m_audioPacketBuffer = nullptr;
        }

        m_videoPacketBufferSize = CGE::CGE_MAX(1024 * 256, width * height * 8);
        m_videoPacketBuffer = (unsigned char *) av_malloc(m_videoPacketBufferSize);

        if (m_hasAudio) {
            m_audioPacketBufferSize = 256 * 1024;
            m_audioPacketBuffer = (unsigned char *) av_malloc(m_audioPacketBufferSize);
        }

        //CGE_LOG_INFO("addStream OK!");

        if (!m_context->pVideoStream || !_openVideo()) {
            CGE_LOG_ERROR("_openVideo failed!\n");
            return false;
        }

        CGE_LOG_INFO("_openVideo OK!");

        if (m_hasAudio) {
            if (!m_context->pAudioStream || !_openAudio()) {
                CGE_LOG_ERROR("_openAudio failed!\n");
                return false;
            }
        }

        if (!(m_context->pOutputFmt->flags & AVFMT_NOFILE)) {
            if (0 > avio_open(&m_context->pFormatCtx->pb, filename, AVIO_FLAG_WRITE)) {
                CGE_LOG_ERROR("could not open file.");
                return false;
            }
        }

        CGE_LOG_INFO("avio_open OK!");

        if (0 > avformat_write_header(m_context->pFormatCtx, nullptr)) {
            CGE_LOG_ERROR("avformat_write_header failed...");
            return false;
        }

        CGE_LOG_INFO("avformat_write_header OK!");

        if (m_context->pVideoFrame)
            m_context->pVideoFrame->pts = 0;

        return true;
    }

    bool CGEVideoEncoderMP4::_openVideo(/*AVFormatContext *oc, AVCodec *codec, AVStream *st*/) {
        int ret;
        AVCodecContext *c = m_context->pVideoStream->codec;
        AVFormatContext *oc = m_context->pFormatCtx;
        AVCodec *codec = m_context->pVideoCodec;

        ret = avcodec_open2(c, codec, nullptr);

        if (ret < 0) {
            CGE_LOG_ERROR("Could not open video codec: %s %d\n", av_err2str(ret), ret);
            return false;
        }

        /* allocate and init a re-usable frame */
        m_context->pVideoFrame = av_frame_alloc();
        if (!m_context->pVideoFrame) {
            CGE_LOG_ERROR("Could not allocate video frame\\n");
            return false;
        }

        /* Allocate the encoded raw picture. */

        ret = avpicture_alloc(&m_context->dstPicture, c->pix_fmt, c->width, c->height);
        if (ret < 0) {
            CGE_LOG_ERROR("Could not allocate picture: %s\n", av_err2str(ret));
            return false;
        }

        /* copy data and linesize picture pointers to frame */
        *((AVPicture *) m_context->pVideoFrame) = m_context->dstPicture;
        return true;
    }

    int CGEVideoEncoderMP4::init_fifo(AVAudioFifo **fifo, AVCodecContext *output_codec_context) {
        /** Create the FIFO buffer based on the specified output sample format. */
        if (!(*fifo = av_audio_fifo_alloc(output_codec_context->sample_fmt,
                                          output_codec_context->channels, 1))) {
            fprintf(stderr, "Could not allocate FIFO\n");
            return AVERROR(ENOMEM);
        }
        return 0;
    }

    bool CGEVideoEncoderMP4::_openAudio() {
        int ret;
        AVCodecContext *c = m_context->pAudioStream->codec;
        AVFormatContext *oc = m_context->pFormatCtx;
        AVCodec *codec = m_context->pAudioCodec;

        AVDictionary *opts = NULL;

        av_dict_set(&opts, "strict", "experimental", 0);

        /* open it */
        ret = avcodec_open2(c, codec, &opts);
        av_dict_free(&opts);

        if (ret < 0) {
            CGE_LOG_ERROR("Could not open audio codec: %s\n", av_err2str(ret));
            return false;
        }

        m_context->maxDstNbSamples = c->codec->capabilities & CODEC_CAP_VARIABLE_FRAME_SIZE ?
                                     10000 : c->frame_size;

        /* create resampler context */
        if (c->sample_fmt != AV_SAMPLE_FMT_S16) {
            auto swr_ctx = swr_alloc();
            m_context->pSwrCtx = swr_ctx;
            if (!m_context->pSwrCtx) {
                CGE_LOG_ERROR("Could not allocate resampler context\n");
                return false;
            }

            /* set options */
            av_opt_set_int(swr_ctx, "in_channel_count", c->channels, 0);
            av_opt_set_int(swr_ctx, "in_sample_rate", c->sample_rate, 0);
            av_opt_set_sample_fmt(swr_ctx, "in_sample_fmt", AV_SAMPLE_FMT_S16, 0);
            av_opt_set_int(swr_ctx, "out_channel_count", c->channels, 0);
            av_opt_set_int(swr_ctx, "out_sample_rate", c->sample_rate, 0);
            av_opt_set_sample_fmt(swr_ctx, "out_sample_fmt", c->sample_fmt, 0);

            /* initialize the resampling context */
            if ((ret = swr_init(swr_ctx)) < 0) {
                CGE_LOG_ERROR("Failed to initialize the resampling context\n");
                return false;
            }

            /* compute the number of converted samples: buffering is avoided
            * ensuring that the output buffer will contain at least all the
            * converted input samples */
            ret = av_samples_alloc_array_and_samples(&m_context->dstSampleData,
                                                     &m_context->dstSamplesLinesize, c->channels,
                                                     m_context->maxDstNbSamples, c->sample_fmt, 0);
            if (ret < 0) {
                CGE_LOG_ERROR("Could not allocate destination samples\n");
                return false;
            }

            m_context->dstSamplesSize = av_samples_get_buffer_size(NULL, c->channels,
                                                                   m_context->maxDstNbSamples,
                                                                   c->sample_fmt, 0);
        }
        init_fifo(&m_context->fifo, m_context->pAudioStream->codec);
        return true;
    }

    bool CGEVideoEncoderMP4::record(const ImageData &data) {
        AVPixelFormat pixFmt = (AVPixelFormat) m_recordDataFmt;
        AVCodecContext *codecCtx = m_context->pVideoStream->codec;

        // auto tm = getCurrentTimeMillis(), tm2 = tm;

        if (data.data[0] != nullptr) {
            if (pixFmt != codecCtx->pix_fmt || data.width != codecCtx->width ||
                data.height != codecCtx->height) {
                if (!m_context->pSwsCtx) {
                    m_context->pSwsCtx = sws_getContext(data.width, data.height, pixFmt,
                                                        codecCtx->width, codecCtx->height,
                                                        codecCtx->pix_fmt, SWS_POINT, nullptr,
                                                        nullptr, nullptr);
                    if (!m_context->pSwsCtx) {
                        CGE_LOG_ERROR("create sws context failed!");
                        return false;
                    }
                    CGE_LOG_INFO("create sws context success!");
                }

                sws_scale(m_context->pSwsCtx, data.data, data.linesize, 0, codecCtx->height,
                          m_context->dstPicture.data, m_context->dstPicture.linesize);
            }
            else {
                const int sz1 = sizeof(m_context->dstPicture.data) < sizeof(data.data)
                                ? sizeof(m_context->dstPicture.data) : sizeof(data.data);
                const int sz2 = sizeof(m_context->dstPicture.linesize) < sizeof(data.linesize)
                                ? sizeof(m_context->dstPicture.linesize) : sizeof(data.linesize);

                memcpy(m_context->dstPicture.data, data.data, sz1);
                memcpy(m_context->dstPicture.linesize, data.linesize, sz2);
            }

            m_context->pVideoFrame->pts = data.pts;
        }
        else {
            //++m_context->pVideoFrame->pts;
        }

        // tm2 = getCurrentTimeMillis();
        // CGE_LOG_ERROR("转换格式花费时间: %g", (tm2 - tm));
        // tm = tm2;

        if (m_context->pFormatCtx->oformat->flags & AVFMT_RAWPICTURE) {
            AVPacket &pkt = m_context->videoPacket;
            av_init_packet(&pkt);

            pkt.flags |= AV_PKT_FLAG_KEY;
            pkt.stream_index = m_context->pVideoStream->index;
            pkt.data = m_context->dstPicture.data[0];
            pkt.size = sizeof(AVPicture);

            m_mutex.lock();
            auto ret = av_interleaved_write_frame(m_context->pFormatCtx, &pkt);
            m_mutex.unlock();

            if (0 > ret) {
                CGE_LOG_ERROR("ImageData av_interleaved_write_frame error1...");
                return false;
            }
            av_free_packet(&pkt);
        }
        else {
            AVPacket &pkt = m_context->videoPacket;
            int gotPacket;
            av_init_packet(&pkt);
            pkt.data = m_videoPacketBuffer;
            pkt.size = m_videoPacketBufferSize;

            //avcodec_encode_video2 为慢速操作
            if (0 > avcodec_encode_video2(codecCtx, &pkt, data.data[0] == nullptr ? nullptr
                                                                                  : m_context->pVideoFrame,
                                          &gotPacket)) {
                CGE_LOG_ERROR("avcodec_encode_video2 error...\n");
                return false;
            }

            // tm2 = getCurrentTimeMillis();
            // CGE_LOG_ERROR("encode 花费时间: %g", (tm2 - tm));
            // tm = tm2;

            if (gotPacket && pkt.size) {
                // CGE_LOG_ERROR("PTS %d, %d\n", data.pts, pkt.pts);

                if (pkt.pts != AV_NOPTS_VALUE) {
                    pkt.pts = av_rescale_q(pkt.pts, codecCtx->time_base,
                                           m_context->pVideoStream->time_base);
                }
                if (pkt.dts != AV_NOPTS_VALUE) {
                    pkt.dts = av_rescale_q(pkt.dts, codecCtx->time_base,
                                           m_context->pVideoStream->time_base);
                }

                pkt.stream_index = m_context->pVideoStream->index;

                m_mutex.lock();
                auto ret = av_interleaved_write_frame(m_context->pFormatCtx, &pkt);
                m_mutex.unlock();

                // av_free_packet(&pkt);

                // tm2 = getCurrentTimeMillis();
                // CGE_LOG_ERROR("av_interleaved_write_frame 花费时间: %g", (tm2 - tm));
                // tm = tm2;

                if (0 > ret) {
                    CGE_LOG_ERROR("ImageData av_interleaved_write_frame error2... 0x%x\n", ret);
                    return false;
                }

            }
            else if (data.data[0] == nullptr) {
                return false;
            }
        }

        return m_context->pVideoFrame->key_frame != 0;
    }

    //将缓存音频数据， 一次只能存储1024整数倍
    bool CGEVideoEncoderMP4::record(const AudioSampleData &data) {
        assert(m_hasAudio);

        if (data.data[0] != nullptr) {
            assert(m_context->pSwrCtx != nullptr); // 必然resample

            AVCodecContext *audioCodec = m_context->pAudioStream->codec;

            if (m_context->pAudioFrame == nullptr) {
                m_context->pAudioFrame = av_frame_alloc();
            }

            AVFrame *pAudioFrame = m_context->pAudioFrame;
            int srcNbSamples = data.nbSamples[0];

            while (1) {
                int dstNbSamples = m_context->maxDstNbSamples - m_context->dstSampleDataIndex;

                // 4 stands for the output channels.
                auto convertData = m_context->dstSampleData[0] + 4 * m_context->dstSampleDataIndex;

                int ret = swr_convert(m_context->pSwrCtx, &convertData, dstNbSamples,
                                      (const uint8_t **) data.data, srcNbSamples);

                if (ret == 0) {
                    // CGE_LOG_ERROR("ret == 0");
                    break;
                }
                else if (ret < 0) {
                    CGE_LOG_ERROR("Error while converting...\n");
                    return false;
                }

                m_context->dstSampleDataIndex += ret;
                srcNbSamples = 0;

                if (m_context->dstSampleDataIndex >= m_context->maxDstNbSamples) {
                    m_context->dstSampleDataIndex = 0;
                    // CGE_LOG_ERROR("Recording...");
                    pAudioFrame->nb_samples = m_context->maxDstNbSamples;
                    pAudioFrame->quality = audioCodec->global_quality;
                    avcodec_fill_audio_frame(pAudioFrame, audioCodec->channels,
                                             audioCodec->sample_fmt, m_context->dstSampleData[0],
                                             m_context->dstSamplesSize, 0);
                    pAudioFrame->data[0] = m_context->dstSampleData[0];
                    pAudioFrame->linesize[0] = m_context->dstSamplesSize;
                    recordAudioFrame(pAudioFrame);
                }
            }

            return pAudioFrame->key_frame != 0;
        }

        return recordAudioFrame(nullptr);
    }

    int CGEVideoEncoderMP4::init_input_frame(AVFrame **frame) {
        if (!(*frame = av_frame_alloc())) {
            fprintf(stderr, "Could not allocate input frame\n");
            return AVERROR(ENOMEM);
        }
        return 0;
    }

    void CGEVideoEncoderMP4::init_packet(AVPacket *packet) {
        av_init_packet(packet);
        /** Set the packet data and size so that it is recognized as being empty. */
        packet->data = NULL;
        packet->size = 0;
    }

    int CGEVideoEncoderMP4::decode_audio_frame(AVFrame *frame,
                                  AVFormatContext *input_format_context,
                                  AVCodecContext *input_codec_context,
                                  int *data_present, int *finished) {
        /** Packet used for temporary storage. */
        AVPacket input_packet;
        int error;
        init_packet(&input_packet);

        if ((error = av_read_frame(input_format_context, &input_packet)) < 0) {
            if (error == AVERROR_EOF)
                *finished = 1;
            else {
                /*fprintf(stderr, "Could not read frame (error '%s')\n",
                        get_error_text(error));*/
                return error;
            }
        }

        if ((error = avcodec_decode_audio4(input_codec_context, frame,
                                           data_present, &input_packet)) < 0) {
            /*fprintf(stderr, "Could not decode frame (error '%s')\n",
                    get_error_text(error));*/
            av_packet_unref(&input_packet);
            return error;
        }

        /**
         * If the decoder has not been flushed completely, we are not finished,
         * so that this function has to be called again.
         */
        if (*finished && *data_present)
            *finished = 0;
        av_packet_unref(&input_packet);
        return 0;
    }

    int CGEVideoEncoderMP4::init_converted_samples(uint8_t ***converted_input_samples,
                                      AVCodecContext *output_codec_context,
                                      int frame_size) {
        int error;

        /**
         * Allocate as many pointers as there are audio channels.
         * Each pointer will later point to the audio samples of the corresponding
         * channels (although it may be NULL for interleaved formats).
         */
        if (!(*converted_input_samples = (uint8_t **) calloc(output_codec_context->channels,
                                                             sizeof(**converted_input_samples)))) {
            fprintf(stderr, "Could not allocate converted input sample pointers\n");
            return AVERROR(ENOMEM);
        }

        /**
         * Allocate memory for the samples of all channels in one consecutive
         * block for convenience.
         */
        if ((error = av_samples_alloc(*converted_input_samples, NULL,
                                      output_codec_context->channels,
                                      frame_size,
                                      output_codec_context->sample_fmt, 0)) < 0) {
            /*fprintf(stderr,
                    "Could not allocate converted input samples (error '%s')\n",
                    get_error_text(error));*/
            av_freep(&(*converted_input_samples)[0]);
            free(*converted_input_samples);
            return error;
        }
        return 0;
    }

    int CGEVideoEncoderMP4::convert_samples(const uint8_t **input_data,
                               uint8_t **converted_data, const int frame_size,
                               SwrContext *resample_context) {
        int error;

        /** Convert the samples using the resampler. */
        if ((error = swr_convert(resample_context,
                                 converted_data, frame_size,
                                 input_data, frame_size)) < 0) {
            /*fprintf(stderr, "Could not convert input samples (error '%s')\n",
                    get_error_text(error));*/
            return error;
        }

        return 0;
    }

    /** Add converted input audio samples to the FIFO buffer for later processing. */
    int CGEVideoEncoderMP4::add_samples_to_fifo(AVAudioFifo *fifo,
                                   uint8_t **converted_input_samples,
                                   const int frame_size) {
        int error;

        /**
         * Make the FIFO as large as it needs to be to hold both,
         * the old and the new samples.
         */
        if ((error = av_audio_fifo_realloc(fifo, av_audio_fifo_size(fifo) + frame_size)) < 0) {
            fprintf(stderr, "Could not reallocate FIFO\n");
            return error;
        }

        /** Store the new samples in the FIFO buffer. */
        if (av_audio_fifo_write(fifo, (void **) converted_input_samples,
                                frame_size) < frame_size) {
            fprintf(stderr, "Could not write data to FIFO\n");
            return AVERROR_EXIT;
        }
        return 0;
    }

    /**
 * Read one audio frame from the input file, decodes, converts and stores
 * it in the FIFO buffer.
 */
    int CGEVideoEncoderMP4::read_decode_convert_and_store(AVAudioFifo *fifo,
                                             AVFormatContext *input_format_context,
                                             AVCodecContext *input_codec_context,
                                             AVCodecContext *output_codec_context,
                                             SwrContext *resampler_context,
                                             int *finished) {
        /** Temporary storage of the input samples of the frame read from the file. */
        AVFrame *input_frame = NULL;
        /** Temporary storage for the converted input samples. */
        uint8_t **converted_input_samples = NULL;
        int data_present;
        int ret = 0;

        /** Initialize temporary storage for one input frame. */
        if (init_input_frame(&input_frame))
            goto cleanup;
        /** Decode one frame worth of audio samples. */
        if (decode_audio_frame(input_frame, input_format_context,
                               input_codec_context, &data_present, finished))
            goto cleanup;
        /**
         * If we are at the end of the file and there are no more samples
         * in the decoder which are delayed, we are actually finished.
         * This must not be treated as an error.
         */
        if (*finished && !data_present) {
            ret = 0;
            goto cleanup;
        }
        /** If there is decoded data, convert and store it */
        if (data_present) {
            /** Initialize the temporary storage for the converted input samples. */
            if (init_converted_samples(&converted_input_samples, output_codec_context,
                                       input_frame->nb_samples))
                goto cleanup;

            /**
             * Convert the input samples to the desired output sample format.
             * This requires a temporary storage provided by converted_input_samples.
             */
            if (convert_samples((const uint8_t **) input_frame->extended_data,
                                converted_input_samples,
                                input_frame->nb_samples, resampler_context))
                goto cleanup;

            /** Add the converted input samples to the FIFO buffer for later processing. */
            if (add_samples_to_fifo(fifo, converted_input_samples,
                                    input_frame->nb_samples))
                goto cleanup;
            ret = 0;
        }
        ret = 0;

        cleanup:
        if (converted_input_samples) {
            av_freep(&converted_input_samples[0]);
            free(converted_input_samples);
        }
        av_frame_free(&input_frame);

        return ret;
    }

    int CGEVideoEncoderMP4::init_output_frame(AVFrame **frame,
                                 AVCodecContext *output_codec_context,
                                 int frame_size) {
        int error;
        if (!(*frame = av_frame_alloc())) {
            CGE_LOG_ERROR("Could not allocate output frame\n");
            return 0;
        }
        (*frame)->nb_samples = frame_size;
        (*frame)->channel_layout = av_get_default_channel_layout(2);
        (*frame)->format = output_codec_context->sample_fmt;
        (*frame)->sample_rate = output_codec_context->sample_rate;
        if ((error = av_frame_get_buffer(*frame, 0)) < 0) {
            CGE_LOG_ERROR("Could not allocate output frame samples (error '%s')\n",
                          av_err2str(error));
            av_frame_free(frame);
            return error;
        }

        return 0;
    }

/** Encode one frame worth of audio to the output file. */
    int CGEVideoEncoderMP4::encode_audio_frame(AVFrame *frame,
                                  AVFormatContext *output_format_context,
                                  AVCodecContext *output_codec_context,
                                  int *data_present) {
        /** Packet used for temporary storage. */
        AVPacket output_packet;
        int error;
        init_packet(&output_packet);

        /** Set a timestamp based on the sample rate for the container. */
        if (frame) {
            frame->pts = pts;
            pts += frame->nb_samples;
        }

        if (frame->pts > 477312)
            return 1;
        /**
         * Encode the audio frame and store it in the temporary packet.
         * The output audio stream encoder is used to do this.
         */
        if ((error = avcodec_encode_audio2(output_codec_context, &output_packet,
                                           frame, data_present)) < 0) {
            /*CGE_LOG_ERROR("Could not encode frame (error '%s')\n",
                          get_error_text(error));*/
            av_packet_unref(&output_packet);
            return error;
        }

        /** Write one audio frame from the temporary packet to the output file. */
        if (*data_present) {
            output_packet.stream_index = m_context->pAudioStream->index;

            m_mutex.lock();
            error = av_interleaved_write_frame(output_format_context, &output_packet);
            m_mutex.unlock();
            if (error < 0) {
                /*CGE_LOG_ERROR("Could not write frame (error '%s')\n",
                              get_error_text(error));*/
                av_packet_unref(&output_packet);
                return error;
            }

            av_packet_unref(&output_packet);
        }

        return 0;
    }

    int CGEVideoEncoderMP4::load_encode_and_write(AVAudioFifo *fifo,
                                     AVFormatContext *output_format_context,
                                     AVCodecContext *output_codec_context) {
        int ret = 0;
        /** Temporary storage of the output samples of the frame written to the file. */
        AVFrame *output_frame;
        /**
         * Use the maximum number of possible samples per frame.
         * If there is less than the maximum possible frame size in the FIFO
         * buffer use this number. Otherwise, use the maximum possible frame size
         */
        const int frame_size = FFMIN(av_audio_fifo_size(fifo),
                                     output_codec_context->frame_size);
        int data_written;

        /** Initialize temporary storage for one output frame. */

        if (init_output_frame(&output_frame, output_codec_context, frame_size))
            return -1;

        /**
         * Read as many samples from the FIFO buffer as required to fill the frame.
         * The samples are stored in the frame temporarily.
         */
        if (av_audio_fifo_read(fifo, (void **) output_frame->data, frame_size) < frame_size) {
            CGE_LOG_ERROR("Could not read data from FIFO\n");
            av_frame_free(&output_frame);
            return -1;
        }

        /** Encode one frame worth of audio samples. */
        if ((ret = encode_audio_frame(output_frame, output_format_context,
                               output_codec_context, &data_written)) < 0) {
            CGE_LOG_ERROR("encode_audio_frame");
            av_frame_free(&output_frame);
            return -1;
        }
        av_frame_free(&output_frame);
        return ret;
    }

    int CGEVideoEncoderMP4::decodeAudioFile(const char *path) {
        AVFormatContext *format = avformat_alloc_context();
        CGE_LOG_ERROR("Could not open file %s\n", path);

        int ret;
        if ((ret = avformat_open_input(&format, path, NULL, NULL)) < 0) {
            CGE_LOG_ERROR("Could not open file %s %d\n", av_err2str(ret), ret);
            return -1;
        }
        if (avformat_find_stream_info(format, NULL) < 0) {
            CGE_LOG_ERROR("Could not retrieve stream info from file '%s'\n");
            return -1;
        }

        // Find the index of the first audio stream
        int stream_index = -1;
        for (int i = 0; i < format->nb_streams; i++) {
            if (format->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_AUDIO) {
                stream_index = i;
                break;
            }
        }
        if (stream_index == -1) {
            return -1;
        }
        AVStream *stream = format->streams[stream_index];

        // find & open codec
        AVCodecContext *iCodecCx = stream->codec;
        if (avcodec_open2(iCodecCx, avcodec_find_decoder(iCodecCx->codec_id), NULL) < 0) {
            return -1;
        }

        while (1) {
            /** Use the encoder's desired frame size for processing. */
            const int output_frame_size = m_context->pAudioStream->codec->frame_size;
            int finished = 0;
            while (av_audio_fifo_size(m_context->fifo) < output_frame_size) {
                if (read_decode_convert_and_store(m_context->fifo, format,
                                                  iCodecCx,
                                                  m_context->pAudioStream->codec,
                                                  m_context->pSwrCtx, &finished))
//                    goto cleanup;
                    return 0;

                if (finished)
                    break;
            }

            int encodeRes;
            while (av_audio_fifo_size(m_context->fifo) >= output_frame_size ||
                   (finished && av_audio_fifo_size(m_context->fifo) > 0)) {

                m_context->pAudioStream->codec->sample_rate = iCodecCx->sample_rate;
                if (m_context->pAudioStream->pts.val > m_context->pVideoStream->pts.val) {

                }
                if ((encodeRes = load_encode_and_write(m_context->fifo, m_context->pFormatCtx,
                                                       m_context->pAudioStream->codec)) < 0) {
                    return 0;
                }
                if (encodeRes > 0) {
                    finished = 1;
                    break;
                }
            }

            if (finished) {
                return 0;
            }

            if (finished) {
                int data_written;
                do {
                    if (encode_audio_frame(NULL, m_context->pFormatCtx,
                                           m_context->pAudioStream->codec, &data_written))
                        return 0;
                } while (data_written);
                break;
            }
        }
        CGE_LOG_ERROR("finish audio ");

      /*  if (m_context->fifo)
            av_audio_fifo_free(m_context->fifo);
        avcodec_close(iCodecCx);
        avformat_free_context(format);*/
        return 0;

    }

    bool CGEVideoEncoderMP4::recordAudioFrame(AVFrame *pAudioFrame) {
        assert(m_hasAudio);

        AVPacket pkt = {0};
        av_init_packet(&pkt);
        pkt.data = m_audioPacketBuffer;
        pkt.size = m_audioPacketBufferSize;

        int gotPacket;

        int ret = avcodec_encode_audio2(m_context->pAudioStream->codec, &pkt, pAudioFrame,
                                        &gotPacket);

        if (ret < 0) {
            CGE_LOG_ERROR("Error encoding audio frame: %s\n", av_err2str(ret));
            return false;
        }

        if (!gotPacket || pkt.size == 0)
            return false;

        pkt.stream_index = m_context->pAudioStream->index;

        m_mutex.lock();
        ret = av_interleaved_write_frame(m_context->pFormatCtx, &pkt);
        m_mutex.unlock();

        // av_free_packet(&pkt);

        if (ret != 0) {
            CGE_LOG_ERROR("---Error while writing audio frame: %s\n", av_err2str(ret));
            return false;
        }

        return true;
    }

    bool CGEVideoEncoderMP4::recordVideoFrame(AVFrame *pVideoFrame) {
        AVCodecContext *codecCtx = m_context->pVideoStream->codec;

        AVPacket &pkt = m_context->videoPacket;
        int gotPacket;
        av_init_packet(&pkt);
        pkt.data = m_videoPacketBuffer;
        pkt.size = m_videoPacketBufferSize;

        //avcodec_encode_video2 为慢速操作
        if (0 > avcodec_encode_video2(codecCtx, &pkt, pVideoFrame, &gotPacket)) {
            CGE_LOG_ERROR("avcodec_encode_video2 error...\n");
            return false;
        }

        // tm2 = getCurrentTimeMillis();
        // CGE_LOG_ERROR("encode 花费时间: %g", (tm2 - tm));
        // tm = tm2;

        if (gotPacket && pkt.size) {
            // CGE_LOG_ERROR("PTS %d, %d\n", data.pts, pkt.pts);

            if (pkt.pts != AV_NOPTS_VALUE) {
                pkt.pts = av_rescale_q(pkt.pts, codecCtx->time_base,
                                       m_context->pVideoStream->time_base);
            }
            if (pkt.dts != AV_NOPTS_VALUE) {
                pkt.dts = av_rescale_q(pkt.dts, codecCtx->time_base,
                                       m_context->pVideoStream->time_base);
            }

            pkt.stream_index = m_context->pVideoStream->index;

            m_mutex.lock();
            auto ret = av_interleaved_write_frame(m_context->pFormatCtx, &pkt);
            m_mutex.unlock();

            // av_free_packet(&pkt);

            // tm2 = getCurrentTimeMillis();
            // CGE_LOG_ERROR("av_interleaved_write_frame 花费时间: %g", (tm2 - tm));
            // tm = tm2;

            if (0 > ret) {
                CGE_LOG_ERROR("av_interleaved_write_frame error2... 0x%x\n", ret);
                return false;
            }
        }

        return true;;
    }

    bool CGEVideoEncoderMP4::save() {
        {
            ImageData videoData = {0};
            while (record(videoData));
        }

        if (m_hasAudio) {
            AudioSampleData audioData = {0};
            while (record(audioData));
        }

        if (0 != av_write_trailer(m_context->pFormatCtx))
            return false;
        m_context->cleanup();
        return true;
    }

    void CGEVideoEncoderMP4::drop() {
        m_context->cleanup();
    }

    int CGEVideoEncoderMP4::_queryDataFormat(RecordDataFormat fmt) {
        AVPixelFormat result = AV_PIX_FMT_NONE;
        switch (fmt) {
            case FMT_RGBA8888:
                result = AV_PIX_FMT_RGBA;
                break;
            case FMT_RGB565:
                result = AV_PIX_FMT_RGB565;
                break;
            case FMT_BGR24:
                result = AV_PIX_FMT_BGR24;
                break;
            case FMT_GRAY8:
                result = AV_PIX_FMT_GRAY8;
                break;
            case FMT_NV21:
                result = AV_PIX_FMT_NV21;
                break;
            case FMT_YUV420P:
                result = AV_PIX_FMT_YUV420P;
                break;
            default:
                break;
        }
        return result;
    }

    void CGEVideoEncoderMP4::setRecordDataFormat(RecordDataFormat fmt) {
        m_recordDataFmt = _queryDataFormat(fmt);
    }

    double CGEVideoEncoderMP4::getVideoStreamTime() {
        return (m_context && m_context->pVideoStream) ? m_context->pVideoStream->pts.val *
                                                        av_q2d(m_context->pVideoStream->time_base)
                                                      : 0.0;
    }

    double CGEVideoEncoderMP4::getAudioStreamTime() {
        return (m_context && m_context->pAudioStream) ? m_context->pAudioStream->pts.val *
                                                        av_q2d(m_context->pAudioStream->time_base)
                                                      : 0.0;
    }
}


#endif











