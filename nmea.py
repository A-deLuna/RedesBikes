nmea = "$GPGGA,050223.00,3239.31973,N,11524.98422,W,2,12,0.78,11.6,M,-32.3,M,,0000*5C"
import string


def parse_nmea(nmea):
    parts = string.split(nmea,',')
    lat = float(parts[2])
    lng = float(parts[4])
    lat_min = lat % 100
    lng_min = lng % 100
    lat_dec = lat_min / 60 + lat // 100
    lng_dec = lng_min / 60 + lng // 100
    lat_dec = lat_dec * (1 if parts[3] == "N" else -1)
    lng_dec = lng_dec * (1 if parts[5] == "E" else -1)
    return (lat_dec, lng_dec)

