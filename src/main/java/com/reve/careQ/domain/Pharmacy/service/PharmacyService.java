package com.reve.careQ.domain.Pharmacy.service;

import com.reve.careQ.domain.Pharmacy.dto.PharmacyDto;
import com.reve.careQ.domain.Pharmacy.entity.Pharmacy;
import com.reve.careQ.domain.Pharmacy.repository.PharmacyRepository;
import com.reve.careQ.global.ApiKeyConfig.ApiKeys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PharmacyService {
    private final PharmacyRepository pharmacyRepository;
    private final ApiKeys apikeys;

    @PostConstruct
    @Transactional
    public void init() {
        if (pharmacyRepository.count() == 0) {
            updatePharmacies();
        }
    }

    @Transactional(readOnly = true)
    public Optional<Pharmacy> findById(Long id) {
        return pharmacyRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Page<PharmacyDto> getAllPharmacies(Pageable pageable) {
        Page<Pharmacy> pharmacies = pharmacyRepository.findAll(pageable);
        return pharmacies.map(Pharmacy::toResponse);
    }

    @Transactional(readOnly = true)
    public List<String> getAllStates() {
        return pharmacyRepository.findAllStates();
    }

    @Transactional(readOnly = true)
    public List<String> getCitiesByState(String state) {
        return pharmacyRepository.findCitiesByState(state);
    }

    @Transactional(readOnly = true)
    public List<PharmacyDto> getPharmacyByStateAndCity(String state, String city) {
        List<Pharmacy> pharmacies = pharmacyRepository.findByStateAndCity(state, city);
        return convertToPharmacyDtos(pharmacies);
    }

    private List<PharmacyDto> convertToPharmacyDtos(List<Pharmacy> pharmacies) {
        return pharmacies.stream()
                .map(Pharmacy::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updatePharmacies() {
        List<Pharmacy> pharmacies = new ArrayList<>();
        int pageNo = 1;

        while (true) {
            String result = usePharmacyApi(pageNo);
            List<Pharmacy> newPharmacies = parsePharmacyData(result);
            if (newPharmacies.isEmpty()) {
                break;
            }
            pharmacies.addAll(newPharmacies);
            pageNo++;
        }

        pharmacyRepository.saveAll(pharmacies);
    }

    public String usePharmacyApi(int pageNo) {
        String urlStr = "http://apis.data.go.kr/B552657/ErmctInsttInfoInqireService/getParmacyListInfoInqire" +
                "?ServiceKey=" + apikeys.getPharmacyApiKey() +
                "&pageNo=" + pageNo +
                "&numOfRows=100";
        String result;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

            String returnLine;
            StringBuilder resultBuilder = new StringBuilder();

            while ((returnLine = br.readLine()) != null) {
                resultBuilder.append(returnLine).append("\n\r");
            }

            urlConnection.disconnect();
            result = resultBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    private List<Pharmacy> parsePharmacyData(String data) {
        List<Pharmacy> pharmacies = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(data)));

            NodeList nodeList = document.getElementsByTagName("item");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);
                pharmacies.add(createPharmacyFromElement(element));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pharmacies;
    }

    private Pharmacy createPharmacyFromElement(Element element) {
        NodeList dutyNameNode = element.getElementsByTagName("dutyName");
        NodeList dutyAddrNode = element.getElementsByTagName("dutyAddr");
        NodeList dutyTel1Node = element.getElementsByTagName("dutyTel1");
        NodeList wgs84LatNode = element.getElementsByTagName("wgs84Lat");
        NodeList wgs84LonNode = element.getElementsByTagName("wgs84Lon");

        String dutyName = dutyNameNode.getLength() > 0 ? dutyNameNode.item(0).getTextContent() : "";
        String dutyAddr = dutyAddrNode.getLength() > 0 ? dutyAddrNode.item(0).getTextContent() : "";
        String dutyTel1 = dutyTel1Node.getLength() > 0 ? dutyTel1Node.item(0).getTextContent() : "";

        String wgs84LatStr = wgs84LatNode.getLength() > 0 ? wgs84LatNode.item(0).getTextContent() : "";
        double wgs84Lat = wgs84LatStr.isEmpty() ? 0.0 : Double.parseDouble(wgs84LatStr);

        String wgs84LonStr = wgs84LonNode.getLength() > 0 ? wgs84LonNode.item(0).getTextContent() : "";
        double wgs84Lon = wgs84LonStr.isEmpty() ? 0.0 : Double.parseDouble(wgs84LonStr);

        String[] dutyAddrSplit = dutyAddr.split("\\s");
        String state = dutyAddrSplit.length > 0 ? dutyAddrSplit[0] : "";
        String city = dutyAddrSplit.length > 1 ? dutyAddrSplit[1] : "";

        return Pharmacy.builder()
                .name(dutyName)
                .address(dutyAddr)
                .wgs84Lat(wgs84Lat)
                .wgs84Lon(wgs84Lon)
                .state(state)
                .city(city)
                .phone(dutyTel1)
                .build();
    }
}
