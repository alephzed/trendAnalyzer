var convertTrendType = function(trendType) {
    if (trendType === 1) {
        return "down";
    }
    if (trendType === 2) {
        return "up";
    }
}

exports.convertTrendType = convertTrendType;