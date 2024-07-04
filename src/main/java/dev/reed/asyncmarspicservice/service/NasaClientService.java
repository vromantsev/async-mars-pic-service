package dev.reed.asyncmarspicservice.service;

import dev.reed.asyncmarspicservice.config.AppProperties;
import dev.reed.asyncmarspicservice.dto.Photos;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class NasaClientService {

    private final RestClient restClient;
    private final AppProperties appProperties;

    public String findLargestNasaPic(final int sol) {
        return restClient.get()
                .uri(UriComponentsBuilder.newInstance().queryParam("sol", sol).queryParam("api_key", appProperties.getApiKey()).build().toUriString())
                .retrieve()
                .toEntity(Photos.class)
                .getBody()
                .photos()
                .parallelStream()
                .map(photo -> {
                    String url = photo.imgSrc();
                    URI location = restClient.head().uri(url).retrieve().toBodilessEntity().getHeaders().getLocation();
                    long contentLength = restClient.head().uri(location).retrieve().toBodilessEntity().getHeaders().getContentLength();
                    return new PicData(location.toString(), contentLength);
                })
                .max(Comparator.comparingLong(PicData::contentLength))
                .map(PicData::url)
                .orElseThrow();
    }

    public byte[] getNasaPicture(String url) {
        return restClient.get()
                .uri(url)
                .retrieve()
                .toEntity(byte[].class)
                .getBody();
    }

    record PicData(String url, long contentLength) {
    }
}
