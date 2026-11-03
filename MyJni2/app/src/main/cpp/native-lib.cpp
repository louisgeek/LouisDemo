#include <jni.h>
#include <string>
#include <cstdint>
#include <cstring>
#include <android/log.h>
#include <vector>

#define LOG_TAG "IndexData"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

// 录像索引定义
#define ZRT_CLD_INDX_BODY_LEN 24 * 60 * 6


// 索引头结构体
typedef struct {
    uint8_t version;           // 版本号 'V'
    uint8_t index_len[2];      // 录像长度
    uint8_t stream_interval[2]; // 分割
    uint8_t split_symbol;      // 分割符号
    uint8_t cloud_provider[2]; // cloud_provider
    uint8_t source_symbol;     // 源符号
    uint8_t source;           // 数据源 'S'
    uint8_t reserve[6];       // 保留字段
} ZRT_CLD_INDEX_HEAD;

// 索引数据结构体
typedef struct {
    ZRT_CLD_INDEX_HEAD head;                    // 索引头
    uint32_t body[ZRT_CLD_INDX_BODY_LEN];      // 索引体数据
} ZRT_CLD_INDEX_DATA;

// 告警类型位定义
#define ALARM_TYPE_MASK     0xFFFFFFF0  // bit[31:4] 告警类型
#define OFFSET_SECONDS_MASK 0x0000000F  // bit[3:0] 偏移秒数

// 告警类型位域定义
#define ALARM_MOTION_DETECT (1 << 4)    // bit4: 动检
#define ALARM_PIR          (1 << 5)    // bit5: PIR
#define ALARM_KEY          (1 << 6)    // bit6: KEY
// bit[7-31]: 视频相关告警类型，可根据需要扩展

// 告警项结构体
typedef struct {
    uint32_t starttime;
    uint32_t endtime;
    uint32_t typevalue;
} AlarmItem;

// 辅助函数：提取告警类型
inline uint32_t get_alarm_type(uint32_t index_value) {
    return (index_value & ALARM_TYPE_MASK) >> 4;
}

// 辅助函数：提取偏移秒数
inline uint32_t get_offset_seconds(uint32_t index_value) {
    return index_value & OFFSET_SECONDS_MASK;
}

// 辅助函数：构造索引值
inline uint32_t make_index_value(uint32_t alarm_type, uint32_t offset_seconds) {
    return ((alarm_type & 0x0FFFFFFF) << 4) | (offset_seconds & 0x0F);
}

// 转换方法：将字节数据转换为索引结构体
std::vector<AlarmItem> convert_to_index_data(const uint8_t *data, uint32_t len) {

    LOGD("convert_to_index_data len=%zu", len); //预期34576（1day）
    ZRT_CLD_INDEX_DATA index_data = {0};
    std::vector<AlarmItem> alarm_list;

    if (data == nullptr || len < sizeof(ZRT_CLD_INDEX_HEAD)) {
        return alarm_list;
    }

    // 先转换索引头
    memcpy(&index_data.head, data, sizeof(ZRT_CLD_INDEX_HEAD));

    // 打印head数据
    LOGD("Head Data:");
    LOGD("version: %c", index_data.head.version);
    LOGD("index_len: %c%c", index_data.head.index_len[0], index_data.head.index_len[1]);
    LOGD("stream_interval: 0x%02X 0x%02X", index_data.head.stream_interval[0],
         index_data.head.stream_interval[1]);
    LOGD("split_symbol: 0x%02X", index_data.head.split_symbol);
    LOGD("cloud_provider: 0x%02X 0x%02X", index_data.head.cloud_provider[0],
         index_data.head.cloud_provider[1]);
    LOGD("source_symbol: %c", index_data.head.source_symbol);
    LOGD("source: %c", index_data.head.source);

    // 判断版本号是否为V20
    if (index_data.head.version == 'V' && index_data.head.index_len[0] == '2' &&
        index_data.head.index_len[1] == '0') {

        // 检查完整数据长度
        if (len < sizeof(ZRT_CLD_INDEX_DATA)) {
            return alarm_list;
        }

        // 复制索引体
        const uint8_t *body_data =
                data + sizeof(ZRT_CLD_INDEX_HEAD); //指针向前移动ZRT_CLD_INDEX_HEAD长度个字节
        for (int i = 0; i < ZRT_CLD_INDX_BODY_LEN; i++) {
            index_data.body[i] = *((uint32_t *) (body_data + i * 4));
        }

        // 解析body数据，提取有效告警
        for (int i = 0; i < ZRT_CLD_INDX_BODY_LEN; i++) {
            if (index_data.body[i] != 0) {
                AlarmItem item;
                item.starttime = i * 10; // 每个索引代表10秒
                item.endtime = item.starttime + 10 + get_offset_seconds(index_data.body[i]);
                item.typevalue = get_alarm_type(index_data.body[i]);
                alarm_list.push_back(item);

                LOGD("Alarm[%d]: starttime=%u, endtime=%u, typevalue=%u",
                     alarm_list.size() - 1, item.starttime, item.endtime, item.typevalue);
            }
        }
    }
    return alarm_list;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_louis_myjnivid_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    // 模拟索引数据
    uint8_t mock_data[sizeof(ZRT_CLD_INDEX_DATA)];

    // 填充索引头
    ZRT_CLD_INDEX_HEAD *head = (ZRT_CLD_INDEX_HEAD *) mock_data;
    head->version = 'V';
    head->index_len[0] = '2';
    head->index_len[1] = '0';
    head->stream_interval[0] = 0x0A;
    head->stream_interval[1] = 0x00;
    head->split_symbol = 0xFF;
    head->cloud_provider[0] = 0x01;
    head->cloud_provider[1] = 0x02;
    head->source_symbol = 'S';
    head->source = 'L';
    memset(head->reserve, 0, 6);

    // 填充索引体（模拟一些告警数据）
    uint32_t *body = (uint32_t *) (mock_data + sizeof(ZRT_CLD_INDEX_HEAD));
    for (int i = 0; i < ZRT_CLD_INDX_BODY_LEN; i++) {
        if (i % 100 == 0) {
            // 每100个位置模拟一个动检告警
            body[i] = make_index_value(ALARM_MOTION_DETECT >> 4, i % 10);
        } else {
            body[i] = 0;
        }
    }

    // 调用解析函数
    std::vector<AlarmItem> alarm_list = convert_to_index_data(mock_data,
                                                              sizeof(ZRT_CLD_INDEX_DATA));

    LOGD("Total alarms found: %zu", alarm_list.size());

    std::string hello =
            "Index data parsed successfully, found " + std::to_string(alarm_list.size()) +
            " alarms";
    return env->NewStringUTF(hello.c_str());
}