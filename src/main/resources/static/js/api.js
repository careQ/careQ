const stateSelect = document.getElementById('Q0');
const citySelect = document.getElementById('Q1');
const searchButton = document.querySelector('.search-button');
const searchTypeRadios = document.querySelectorAll('.button-toggle-input');
const mapContainer = document.getElementById('map');

var mapOption = {
    center: new kakao.maps.LatLng(33.450701, 126.570667), // 기본 지도 중심 좌표
    level: 6, // 지도 확대 레벨
};

// 지도 생성
var map = new kakao.maps.Map(mapContainer, mapOption);

// 마커를 담을 배열
var markers = [];

// 시도와 구군 옵션을 설정하는 함수
function setCityOptions(state) {
    citySelect.innerHTML = ""; // 기존 옵션 제거
        const cityOptions = {

            서울특별시: ["강남구", "강동구", "강북구", "강서구", "관악구", "광진구", "구로구", "금천구", "노원구", "도봉구", "동대문구", "동작구", "마포구", "서대문구", "서초구", "성동구", "성북구", "송파구", "양천구", "영등포구", "용산구", "은평구", "종로구", "중구", "중랑구"],

            경기도: [ "수원시 장안구", "수원시 권선구", "수원시 팔달구", "수원시 영통구", "성남시 수정구", "성남시 중원구", "성남시 분당구", "의정부시", "안양시 만안구", "안양시 동안구", "부천시", "광명시", "평택시", "동두천시", "안산시 상록구", "안산시 단원구", "고양시 덕양구", "고양시 일산동구", "고양시 일산서구", "과천시", "구리시", "남양주시", "오산시", "시흥시", "군포시", "의왕시", "하남시", "용인시 처인구", "용인시 기흥구", "용인시 수지구", "파주시", "이천시", "안성시", "김포시", "화성시", "광주시", "양주시", "포천시", "여주시", "연천군", "가평군", "양평군" ],
            인천광역시: [ "계양구", "미추홀구", "남동구", "동구", "부평구", "서구", "연수구", "중구", "강화군", "옹진군" ],

            강원도: [ "춘천시", "원주시", "강릉시", "동해시", "태백시", "속초시", "삼척시", "홍천군", "횡성군", "영월군", "평창군", "정선군", "철원군", "화천군", "양구군", "인제군", "고성군", "양양군" ],

            충청북도: [ "청주시 상당구", "청주시 서원구", "청주시 흥덕구", "청주시 청원구", "충주시", "제천시", "보은군", "옥천군", "영동군", "증평군", "진천군", "괴산군", "음성군", "단양군" ],
            충청남도: [ "천안시 동남구", "천안시 서북구", "공주시", "보령시", "아산시", "서산시", "논산시", "계룡시", "당진시", "금산군", "부여군", "서천군", "청양군", "홍성군", "예산군", "태안군" ],
            대전광역시: [ "대덕구", "동구", "서구", "유성구", "중구" ],
            세종특별자치시: [ "세종특별자치시" ],

            전라북도: [ "전주시 완산구", "전주시 덕진구", "군산시", "익산시", "정읍시", "남원시", "김제시", "완주군", "진안군", "무주군", "장수군", "임실군", "순창군", "고창군", "부안군" ],
            전라남도: [ "목포시", "여수시", "순천시", "나주시", "광양시", "담양군", "곡성군", "구례군", "고흥군", "보성군", "화순군", "장흥군", "강진군", "해남군", "영암군", "무안군", "함평군", "영광군", "장성군", "완도군", "진도군", "신안군" ],
            광주광역시: [ "광산구", "남구", "동구", "북구", "서구" ],

            경상북도: [ "포항시 남구", "포항시 북구", "경주시", "김천시", "안동시", "구미시", "영주시", "영천시", "상주시", "문경시", "경산시", "군위군", "의성군", "청송군", "영양군", "영덕군", "청도군", "고령군", "성주군", "칠곡군", "예천군", "봉화군", "울진군", "울릉군" ],
            경상남도: [ "창원시 의창구", "창원시 성산구", "창원시 마산합포구", "창원시 마산회원구", "창원시 진해구", "진주시", "통영시", "사천시", "김해시", "밀양시", "거제시", "양산시", "의령군", "함안군", "창녕군", "고성군", "남해군", "하동군", "산청군", "함양군", "거창군", "합천군" ],
            부산광역시: [ "강서구", "금정구", "남구", "동구", "동래구", "부산진구", "북구", "사상구", "사하구", "서구", "수영구", "연제구", "영도구", "중구", "해운대구", "기장군" ],
            대구광역시: [ "남구", "달서구", "동구", "북구", "서구", "수성구", "중구", "달성군" ],
            울산광역시: [ "남구", "동구", "북구", "중구", "울주군" ],

            제주특별자치도: [ "서귀포시", "제주시" ]

        };

    const cities = cityOptions[state] || [];
    cities.forEach(city => {
        const option = document.createElement('option');
        option.value = city;
        option.textContent = city;
        citySelect.appendChild(option);
    });
}

// 시도 선택 시 구군 옵션 설정
stateSelect.addEventListener('change', function () {
    setCityOptions(stateSelect.value);
});

// 초기 로드 시 시도 선택에 따라 구군 옵션 설정
setCityOptions(stateSelect.value);

// 선택된 검색 유형 (약국 또는 병원) 저장
let selectedSearchType = 'pharmacy';

searchTypeRadios.forEach(radio => {
    radio.addEventListener('change', function () {

        // 검색 버튼을 클릭하면 선택된 검색 유형에 따라 API 요청을 보내기
        selectedSearchType = radio.value;

        const searchIcon = searchButton.querySelector('.search-icon');

    });
});

searchButton.addEventListener('click', function () {
    const selectedState = stateSelect.value;
    const selectedCity = citySelect.value;

    // 기존 결과 초기화
    const listContainer = document.querySelector('.list');
    listContainer.innerHTML = ""; // 기존 목록 제거

    // 기존 마커 초기화
    markers.forEach(marker => {
        marker.setMap(null);

        // 마커 클릭 이벤트 제거
        if (marker.infowindow) {
            marker.infowindow.close();
        }
    });

    markers = [];

    // 약국과 병원을 구분하는 API 요청 주소
    const pharmacyApiUrl = 'http://apis.data.go.kr/B552657/ErmctInsttInfoInqireService/getParmacyListInfoInqire';
    const hospitalApiUrl = 'http://apis.data.go.kr/B552657/HsptlAsembySearchService/getHsptlMdcncListInfoInqire';

    // 선택된 API 주소와 서비스 키를 결정
    const apiUrl = selectedSearchType === 'pharmacy' ? pharmacyApiUrl : hospitalApiUrl;
    const apiKey = selectedSearchType === 'pharmacy' ? pharmacyApiKey : hospitalApiKey;

    // API 요청을 보내고 데이터를 가져오는 로직
    fetch(`${apiUrl}?ServiceKey=${apiKey}&Q0=${selectedState}&Q1=${selectedCity}`)
        .then(response => response.text())
        .then(xmlData => {
            // XML 데이터를 파싱하고 JavaScript 객체로 추출하는 parseXml 함수
            function parseXml(xmlString) {
                const parser = new DOMParser();
                const xmlDoc = parser.parseFromString(xmlString, 'text/xml');
                const items = xmlDoc.getElementsByTagName('item');

                const locations = [];

                for (let i = 0; i < items.length; i++) {
                    const item = items[i];
                    const location = {
                        dutyName: item.getElementsByTagName('dutyName')[0].textContent,
                        dutyAddr: item.getElementsByTagName('dutyAddr')[0].textContent,
                        dutyTel1: item.getElementsByTagName('dutyTel1')[0].textContent,
                        wgs84Lat: parseFloat(item.getElementsByTagName('wgs84Lat')[0].textContent),
                        wgs84Lon: parseFloat(item.getElementsByTagName('wgs84Lon')[0].textContent),
                    };
                    locations.push(location);
                }

                return locations;
            }

            // XML 데이터 파싱
            const locations = parseXml(xmlData);

            if (locations.length === 0) {
                console.error('검색 결과가 없습니다.');
                return;
            }

            // 배열 초기화
            const centerLatLngs = [];

            // 약국 또는 병원 정보를 출력 및 마커를 추가
            locations.forEach(location => {
                const locationItem = document.createElement('div');
                locationItem.classList.add('list-item');
                locationItem.innerHTML = `
                    <div class="list-title">${selectedSearchType === 'pharmacy' ? '약국' : '병원'} : ${location.dutyName}</div>
                    <div class="list-info">번호 : <span class="phone">${location.dutyTel1}</span></div>
                    <div class="list-info">위치 : ${location.dutyAddr}</div>
                `;
                listContainer.appendChild(locationItem);

                // 약국 또는 병원 정보 마커로 추가
                const locationLatLng = new kakao.maps.LatLng(location.wgs84Lat, location.wgs84Lon);
                var marker = new kakao.maps.Marker({
                    position: locationLatLng,
                });
                marker.setMap(map);
                markers.push(marker);

                // 중앙 좌표를 배열에 추가
                centerLatLngs.push(locationLatLng);

                // 마커 클릭 이벤트
                kakao.maps.event.addListener(marker, 'click', function () {
                    markers.forEach(m => {
                        if (m.infowindow) {
                            m.infowindow.close();
                        }
                    });

                    // 클릭한 마커의 약국 또는 병원 이름을 정보창으로 표시
                    var infowindow = new kakao.maps.InfoWindow({
                        content: `<div style="padding:5px;font-size:12px;">${location.dutyName}</div>`
                    });
                    infowindow.open(map, marker);
                    marker.infowindow = infowindow;
                });
            });

            // 중앙 좌표
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