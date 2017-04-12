extern "C"
{
#include "libavcodec/avcodec.h"
#include "libavformat/avformat.h"
#include "libavutil/avutil.h"
#include "libavutil/audio_fifo.h"
#include "libswresample/swresample.h"
#include "libswscale/swscale.h"

#include "libavutil/opt.h"
#include "libavutil/imgutils.h"
#include "libavformat/avio.h"
#include "libavutil/avassert.h"
#include "libavutil/avstring.h"
#include "libavutil/frame.h"
#include "libavutil/opt.h"

}