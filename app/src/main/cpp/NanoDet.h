//
// Create by RangiLyu
// 2020 / 10 / 2
//

#ifndef NANODET_H
#define NANODET_H

#include "net.h"
#include "YoloV5.h"

typedef struct HeadInfo
{
    std::string cls_layer;
    std::string dis_layer;
    int stride;
} HeadInfo;


class NanoDet{
public:
    NanoDet(AAssetManager *mgr, const char *param, const char *bin, bool useGPU);

    ~NanoDet();

    std::vector<BoxInfo> detect(JNIEnv *env, jobject image, float score_threshold, float nms_threshold);
//    std::vector<std::string> labels{"person", "bicycle", "car", "motorcycle", "airplane", "bus", "train", "truck", "boat", "traffic light",
//                                    "fire hydrant", "stop sign", "parking meter", "bench", "bird", "cat", "dog", "horse", "sheep", "cow",
//                                    "elephant", "bear", "zebra", "giraffe", "backpack", "umbrella", "handbag", "tie", "suitcase", "frisbee",
//                                    "skis", "snowboard", "sports ball", "kite", "baseball bat", "baseball glove", "skateboard", "surfboard",
//                                    "tennis racket", "bottle", "wine glass", "cup", "fork", "knife", "spoon", "bowl", "banana", "apple",
//                                    "sandwich", "orange", "broccoli", "carrot", "hot dog", "pizza", "donut", "cake", "chair", "couch",
//                                    "potted plant", "bed", "dining table", "toilet", "tv", "laptop", "mouse", "remote", "keyboard", "cell phone",
//                                    "microwave", "oven", "toaster", "sink", "refrigerator", "book", "clock", "vase", "scissors", "teddy bear",
//                                    "hair drier", "toothbrush"};
    std::vector<std::string> labels{"A", "B", "C", "D", "E", "F", "G", "比心","我爱你","好的","耶","H", "I", "J",
                                    "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
                                    "U", "V", "W", "X", "Y", "Z"};
private:
    void preprocess(JNIEnv *env, jobject image, ncnn::Mat& in);
    void decode_infer(ncnn::Mat& cls_pred, ncnn::Mat& dis_pred, int stride, float threshold, std::vector<std::vector<BoxInfo>>& results, float width_ratio, float height_ratio);
    BoxInfo disPred2Bbox(const float*& dfl_det, int label, float score, int x, int y, int stride, float width_ratio, float height_ratio);

    static void nms(std::vector<BoxInfo>& result, float nms_threshold);

    ncnn::Net *Net;
    int input_size = 320;
    int num_class = 30;
    int reg_max = 7;
    std::vector<HeadInfo> heads_info{
        // cls_pred|dis_pred|stride
        {"cls_pred_stride_8", "dis_pred_stride_8", 8},
        {"cls_pred_stride_16", "dis_pred_stride_16", 16},
        {"cls_pred_stride_32", "dis_pred_stride_32", 32},
    };

public:
    static NanoDet *detector;
    static bool hasGPU;
};


#endif //NANODET_H
