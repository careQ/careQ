package com.reve.careQ.domain.Hospital.service;

import com.reve.careQ.domain.Hospital.entity.Hospital;
import com.reve.careQ.domain.Hospital.repository.HospitalRepository;
import com.reve.careQ.global.ApiKeyConfig.ApiKeys;
import com.reve.careQ.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HospitalService {
    private final HospitalRepository hospitalRepository;

    private final ApiKeys apikeys;

    public Optional<Hospital> findById(Long id) {
        return hospitalRepository.findById(id);
    }

    public Optional<Hospital> findByCode(String code) {
        return hospitalRepository.findByCode(code);
    }

    public Optional<Hospital> findByName(String name) {
        return hospitalRepository.findByName(name);
    }

    @Transactional
    public RsData<Hospital> insert(String code, String name, String state, String city) {

        Hospital hospital = Hospital
                .builder()
                .code(code)
                .name(name)
                .state(state)
                .city(city)
                .build();

        hospitalRepository.save(hospital);

        return RsData.of("S-1", "병원테이블에 삽입되었습니다.", hospital);

    }

    @Transactional
    public RsData<String> useHospitalApi(String id) {
        StringBuilder result = new StringBuilder();

        String urlStr = "http://apis.data.go.kr/B552657/HsptlAsembySearchService/getHsptlBassInfoInqire" +
                "?ServiceKey=" + apikeys.getHospitalApiKey() +
                "&HPID=" + id;

        try {
            URL url = new URL(urlStr);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

            String returnLine;

            while ((returnLine = br.readLine()) != null) {
                result.append(returnLine + "\n\r");
            }

            urlConnection.disconnect();

            return RsData.of("S-1", "병원 API 성공", result.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return RsData.of("F-1", "병원 API 사용 중 다음과 같은 오류가 발생했습니다.오류 : " + e.getMessage());
        }
    }

    @Transactional
    public RsData<String[]> parseXml(String xmlData) {
        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(new InputSource(new StringReader(xmlData)));

            Element itemElement = (Element) document.getElementsByTagName("item").item(0);

            if(itemElement == null){
                return RsData.of("F-1", "존재하지 않는 병원코드 입니다.");
            }

            String dutyName = itemElement.getElementsByTagName("dutyName").item(0).getTextContent();
            String dutyAddr = itemElement.getElementsByTagName("dutyAddr").item(0).getTextContent();
            String code = itemElement.getElementsByTagName("hpid").item(0).getTextContent();
            String state = dutyAddr.split("\\s")[0];
            String city = dutyAddr.split("\\s")[1];

            String[] hospital = {code, dutyName, state, city};

            return RsData.of("S-1", "병원테이블에 삽입되었습니다.", hospital);
            } catch (Exception e) {
                e.printStackTrace();
                return RsData.of("F-1", "XML 파싱 중 다음과 같은 오류가 발생했습니다.오류 : "+e.getMessage());
            }
    }

}
