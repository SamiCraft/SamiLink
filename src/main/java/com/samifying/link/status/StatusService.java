package com.samifying.link.status;

import com.pequla.server.ping.ServerPing;
import com.pequla.server.ping.StatusResponse;
import com.samifying.link.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.xbill.DNS.*;
import org.xbill.DNS.Record;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

@Service
public class StatusService {

    private final Logger logger = LoggerFactory.getLogger(StatusService.class);

    public ResponseEntity<StatusResponse> getServerStatus(String hostname, int port) {
        try {
            logger.info("Retrieving status for " + hostname);
            InetAddress address = InetAddress.getByName(hostname);
            ServerPing ping = new ServerPing(new InetSocketAddress(address, port));
            return ResponseEntity.ok(ping.fetchData());
        } catch (IOException e) {
            logger.error("Could not get status for " + hostname);
            logger.error(e.getMessage(), e);
        }
        return ResponseEntity.notFound().build();
    }

    public ResponseEntity<StatusResponse> getServerStatus(String hostname) {
        String query = "_minecraft._tcp." + hostname;
        try {
            Lookup lookup = new Lookup(query, Type.SRV);
            Record[] records = lookup.run();
            if (records == null) {
                return getServerStatus(hostname, 25565);
            }
            for (Record record : records) {
                SRVRecord srv = (SRVRecord) record;
                hostname = srv.getTarget().toString().replaceFirst("\\.$", "");
                return getServerStatus(hostname, srv.getPort());
            }
        } catch (TextParseException e) {
            logger.error("Hostname text is in invalid format");
            logger.error(e.getMessage(), e);
        }
        return ResponseEntity.notFound().build();
    }

    public ResponseEntity<StatusResponse> getDefaultServerStatus() {
        return getServerStatus(AppConstants.MINECRAFT_SERVER_ADDRESS, AppConstants.MINECRAFT_SERVER_PORT);
    }

}
