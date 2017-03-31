/*
* cgeVideoEncoder.h
*
*  Created on: 2015-7-30
*      Author: Wang Yang
*        Mail: admin@wysaid.org
*/

#if !defined(_VIDEO_ENCODER_H_) && defined(_CGE_USE_FFMPEG_)
#define _CGEVIDEO_ENCODER_H_

#include <string>
#include <fstream>
#include <mutex>
#include "cgeFFmpegHeaders.h"

#define AUDIO_INBUF_SIZE 20480

#define AUDIO_REFILL_THRESH 4096

struct AVFrame;

namespace CGE
{
	struct CGEEncoderContextMP4;

	// 使用h264编码视频，FLTP浮点单声道音频的mp4 encoder
	class CGEVideoEncoderMP4
	{
	public:
		CGEVideoEncoderMP4();
		~CGEVideoEncoderMP4();

		enum RecordDataFormat
		{
			FMT_RGBA8888,
			FMT_RGB565,
			FMT_BGR24,
			FMT_GRAY8,
			FMT_NV21,
			FMT_YUV420P
		};

		struct ImageData
		{
			const unsigned char* data[8];
			int width, height;
			int linesize[8];
			long pts;
		};

		struct AudioSampleData
		{
			const unsigned short* data[8];
			int nbSamples[8]; //声音帧大小
			int channels; //声音包含几个通道
		};

		bool init(const char* filename, int fps, int width, int height, bool hasAudio = true, int bitRate = 1650000);

		void setRecordDataFormat(RecordDataFormat fmt);

		//两个record写入文件时将保证线程安全
		bool record(const ImageData& data);
		bool record(const AudioSampleData& data);

		bool recordAudioFrame(AVFrame*);
		bool recordVideoFrame(AVFrame*);

		double getVideoStreamTime();
		double getAudioStreamTime();

		//保存视频
		bool save();

		//丢弃视频
		void drop();

		int addAudio(const char *filename);

		int open_input_file(const char *filename);

		int audioDecode(const char *filename);
		int audioDecode2(const char *filename);
		int decodeAudioFile(const char *path);
		int recordAudioBYFrame(AVFrame *pAudioFrame);
		int audioDecodeRecord(const char *filename);
		std::string srcM;


		int init_fifo(AVAudioFifo **fifo, AVCodecContext *output_codec_context);

		int load_encode_and_write(AVAudioFifo *fifo, AVFormatContext *output_format_context,
								  AVCodecContext *output_codec_context);

		int encode_audio_frame(AVFrame *frame, AVFormatContext *output_format_context,
							   AVCodecContext *output_codec_context, int *data_present);

		int init_output_frame(AVFrame **frame, AVCodecContext *output_codec_context, int frame_size);

		int read_decode_convert_and_store(AVAudioFifo *fifo, AVFormatContext *input_format_context,
										  AVCodecContext *input_codec_context,
										  AVCodecContext *output_codec_context,
										  SwrContext *resampler_context, int *finished);

		int add_samples_to_fifo(AVAudioFifo *fifo, uint8_t **converted_input_samples, const int frame_size);


		int init_input_frame(AVFrame **frame);

		void init_packet(AVPacket *packet);

		int decode_audio_frame(AVFrame *frame, AVFormatContext *input_format_context,
							   AVCodecContext *input_codec_context, int *data_present, int *finished);

		int init_converted_samples(uint8_t ***converted_input_samples, AVCodecContext *output_codec_context,
								   int frame_size);

		int convert_samples(const uint8_t **input_data, uint8_t **converted_data, const int frame_size,
							SwrContext *resample_context);
	protected:
		bool _openVideo();
		bool _openAudio();
		int _queryDataFormat(RecordDataFormat fmt);

	private:
		CGEEncoderContextMP4* m_context;
		std::string m_filename;
		int m_recordDataFmt;
		std::mutex m_mutex;

		unsigned char* m_videoPacketBuffer;
		int m_videoPacketBufferSize;
		unsigned char* m_audioPacketBuffer;
		int m_audioPacketBufferSize;

		bool m_hasAudio;
	};

}



#endif
