// var svgPaths = []

    var svgElements = [];
    var onScreenLimit = 20;
    var onScreenFirst = 0;
    var onScreenEnd = Math.min(onScreenLimit, svgPaths.length);
    var chartSection;
    var state = 0;
    var elementWidth = 400;

    function buildBrowser() {
        chartSection = document.getElementById("chart");

        for (var i = 0; i < svgPaths.length; ++i) {
            svgElements.push(getSvgElement(svgPaths[i]));
        }

        for (var i = onScreenFirst; i < onScreenEnd; ++i) {
            chartSection.appendChild(svgElements[i]);
        }

        document.body.onscroll = updateChart;
    }

    function getSvgElement(svgPath) {
        var svgObject = document.createElement("object");
        var svgDiv = document.createElement("div");

        svgObject.setAttribute("type", "image/svg+xml");
        svgObject.setAttribute("data", svgPath);
        svgDiv.appendChild(svgObject);

        return svgDiv;
    }

    var prev = 0;

    function updateChart() {
        document.body.onscroll = undefined;

        state = document.body.scrollTop - prev;
        prev = document.body.scrollTop;
        if (state > 0 && onScreenEnd < svgElements.length) {
            if (document.body.scrollTop > elementWidth * onScreenLimit * 3 / 4) {
                clearChart();
                onScreenEnd = Math.min(onScreenEnd + onScreenLimit / 2, svgElements.length);
                onScreenFirst = onScreenEnd - onScreenLimit;
                drawChart();
            }
        } else if (state < 0 && onScreenFirst > 0) {
            if (document.body.scrollHeight - document.body.scrollTop > elementWidth * onScreenLimit * 3 / 4) {
                clearChart();
                onScreenFirst = Math.max(onScreenFirst - onScreenLimit / 2, 0);
                onScreenEnd = onScreenFirst + onScreenLimit;
                drawChart();
            }
        }

        document.body.onscroll = updateChart;
    }

    function clearChart() {
        for (var i = onScreenFirst; i < onScreenEnd; ++i) {
            chartSection.removeChild(svgElements[i]);
        }
    }

    function drawChart() {
        for (var i = onScreenFirst; i < onScreenEnd; ++i) {
            chartSection.appendChild(svgElements[i]);
        }
    }

    buildBrowser();
