/****************************************************************************
**
** Copyright (C) 2015 The Qt Company Ltd
** All rights reserved.
** For any questions to The Qt Company, please use contact form at http://qt.io
**
** This file is part of the Qt Charts module.
**
** Licensees holding valid commercial license for Qt may use this file in
** accordance with the Qt License Agreement provided with the Software
** or, alternatively, in accordance with the terms contained in a written
** agreement between you and The Qt Company.
**
** If you have questions regarding the use of this file, please use
** contact form at http://qt.io
**
****************************************************************************/

import QtQuick 2.0
import QtCharts 2.0


ChartView {
    width: 300
    height: 300

    HorizontalStackedBarSeries {
        name: "HorizontalStackedBarSeries"
        BarSet { label: "Set1"; values: [2, 2, 3] }
        BarSet { label: "Set2"; values: [5, 1, 2] }
        BarSet { label: "Set3"; values: [3, 5, 8] }
    }
}
