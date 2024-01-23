const stateSelect = document.getElementById('Q0');
const citySelect = document.getElementById('Q1');
const searchButton = document.querySelector('.search-button');
const listContainer = document.querySelector('.list');

const mapOption = {
    center: new kakao.maps.LatLng(33.450701, 126.570667),
    level: 6,
};

// 지도 생성
const map = new kakao.maps.Map(document.getElementById('map'), mapOption);
let markers = [];

function addOption(select, text) {
    const option = document.createElement('option');
    option.value = text;
    option.textContent = text;
    select.appendChild(option);
}

function setCityOptions(selectedState) {
    fetch(`/members/searches/pharmacies/states/${selectedState}`)
        .then(response => {
            return response.json();
        })
        .then(data => {
            citySelect.innerHTML = "";
            data.data.forEach(city => addOption(citySelect, city));
        });
}

function createMarker(location) {
    const locationLatLng = new kakao.maps.LatLng(location.wgs84Lat, location.wgs84Lon);
    const marker = new kakao.maps.Marker({ position: locationLatLng });
    marker.setMap(map);
    markers.push(marker);
    return marker;
}

function addPharmacyInfo(location) {
    const locationItem = document.createElement('div');
    locationItem.classList.add('list-item');
    locationItem.innerHTML = `
        <div class="list-title">약국 : ${location.name}</div>
        <div class="list-info">위치 : ${location.address}</div>
        <div class="list-info">번호 : <span class="phone">${location.phone}</span></div>
    `;
    listContainer.appendChild(locationItem);

    const marker = createMarker(location);
    kakao.maps.event.addListener(marker, 'click', function () {
        markers.forEach(m => {
            if (m !== marker && m.infowindow) {
                m.infowindow.close();
            }
        });

        var infowindow = new kakao.maps.InfoWindow({
            content: `<div style="padding:5px;font-size:12px;">${location.name}</div>`
        });
        infowindow.open(map, marker);
        marker.infowindow = infowindow;
    });
    return marker.getPosition();
}

// 초기 로드 시 시도 선택에 따라 구군 옵션 설정
if (stateSelect.value) {
    setCityOptions(stateSelect.value);
}

fetch('/members/searches/pharmacies/states')
    .then(response => response.json())
    .then(data => {
        data.data.forEach(state => addOption(stateSelect, state));
        setCityOptions(stateSelect.value);
    });

stateSelect.addEventListener('change', function() {
    setCityOptions(this.value);
});

// 페이지 로드 시 모든 약국 정보 가져오기
fetch('/members/searches/pharmacies?page=0&size=10')
    .then(response => response.json())
    .then(data => {
        const locations = data.data.content;
        const centerLatLngs = locations
            .filter(location => location.wgs84Lat !== 0 && location.wgs84Lon !== 0)
            .map(addPharmacyInfo);

        if (centerLatLngs.length > 0) {
            var bounds = new kakao.maps.LatLngBounds();
            centerLatLngs.forEach(latlng => bounds.extend(latlng));
            map.setBounds(bounds);
        }
    })
    .catch(error => {
        console.error('API 요청에 실패했습니다:', error);
    });

// 검색 버튼 클릭 시 선택된 시도/구군에 위치한 약국 정보 가져오기
searchButton.addEventListener('click', function () {
    while (listContainer.firstChild) {
        listContainer.removeChild(listContainer.firstChild);
    }

    markers.forEach((marker) => {
        marker.setMap(null);
        if (marker.infowindow) {
            marker.infowindow.close();
        }
    });
    markers = [];

    let selectedState = stateSelect.value;
    let selectedCity = citySelect.value;

    let apiUrl;

    // 사용자가 시도/구군을 선택하지 않았을 경우
    if (!selectedState && !selectedCity) {
        apiUrl = '/members/searches/pharmacies?page=0&size=10';
    } else {
        apiUrl = `/members/searches/pharmacies/states/${selectedState}/${selectedCity}`;
    }

    fetch(apiUrl)
        .then(response => response.json())
        .then(data => {
            const locations = (!selectedState && !selectedCity) ? data.data.content : data.data;

            if (locations.length === 0) {
                console.error('검색 결과가 없습니다.');
                return;
            }

            const centerLatLngs = locations
                .filter(location => location.wgs84Lat !== 0 && location.wgs84Lon !== 0)
                .map(addPharmacyInfo);

            if (centerLatLngs.length > 0) {
                var bounds = new kakao.maps.LatLngBounds();
                centerLatLngs.forEach(latlng => bounds.extend(latlng));
                map.setBounds(bounds);
            }
        })
        .catch(error => {
            console.error('API 요청에 실패했습니다:', error);
        });
});

