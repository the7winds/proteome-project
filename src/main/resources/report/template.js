// var svgPaths = []

    var svgElements = [];
    var loadStep = 10;
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
    }

    function getSvgElement(svgPath) {
        var svgObject = document.createElement("object");
        var svgDiv = document.createElement("div");

        svgObject.setAttribute("type", "image/svg+xml");
        svgObject.setAttribute("data", svgPath);
        svgDiv.appendChild(svgObject);

        return svgDiv;
    }

    function loadMore() {
        var up = Math.min(onScreenFirst + loadStep, svgElements.length - onScreenLimit);

        if (onScreenFirst < up) {

            for (var i = onScreenFirst; i < up; ++i) {
                chartSection.removeChild(svgElements[i]);
            }

            onScreenFirst = up;

            var down = Math.min(onScreenEnd + loadStep, svgElements.length);

            for (var i = onScreenEnd; i < down; ++i) {
                chartSection.appendChild(svgElements[i]);
            }

            onScreenEnd = down;
        }
    }

    function loadPrevious() {
        var down = Math.max(onScreenEnd - loadStep, onScreenLimit);

        if (onScreenEnd > down) {

            for (var i = onScreenEnd - 1; i > down ; --i) {
                chartSection.removeChild(svgElements[i]);
            }

            onScreenEnd = down;

            var up = Math.max(onScreenFirst - loadStep, 0);

            for (var i = onScreenFirst; i >= up; --i) {
                chartSection.insertBefore(svgElements[i], chartSection.firstChild);
            }

            onScreenFirst = up;
        }
    }

    buildBrowser();
