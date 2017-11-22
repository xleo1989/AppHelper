package com.x.leo.apphelper.utils;

import java.io.Serializable;
import java.util.List;

/**
 * @作者:My
 * @创建日期: 2017/7/20 14:01
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class GeoLocationBean implements Serializable{
    /**
     * results : [{"address_components":[{"long_name":"248","short_name":"248","types":["street_number"]},{"long_name":"Jalan Tomang Utara II","short_name":"Jl. Tomang Utara II","types":["route"]},{"long_name":"","short_name":"","types":["political"]},{"long_name":"","short_name":"","types":["political"]},{"long_name":"Tomang","short_name":"Tomang","types":["administrative_area_level_4","political"]},{"long_name":"Grogol petamburan","short_name":"Grogol petamburan","types":["administrative_area_level_3","political"]},{"long_name":"Kota Jakarta Barat","short_name":"Kota Jakarta Barat","types":["administrative_area_level_2","political"]},{"long_name":"Daerah Khusus Ibukota Jakarta","short_name":"Daerah Khusus Ibukota Jakarta","types":["administrative_area_level_1","political"]},{"long_name":"印度尼西亚","short_name":"ID","types":["country","political"]},{"long_name":"11440","short_name":"11440","types":["postal_code"]}],"formatted_address":"Jl. Tomang Utara II No.248, Tomang, Grogol petamburan, Kota Jakarta Barat, Daerah Khusus Ibukota Jakarta 11440印度尼西亚","geometry":{"location":{"lat":-6.169963,"lng":106.7998999},"location_type":"ROOFTOP","viewport":{"northeast":{"lat":-6.168614019708498,"lng":106.8012488802915},"southwest":{"lat":-6.171311980291502,"lng":106.7985509197085}}},"place_id":"ChIJrWe4g2b2aS4RbOkm72qb5x0","types":["street_address"]},{"address_components":[{"long_name":"West Jakarta","short_name":"West Jakarta","types":["locality","political"]},{"long_name":"Kebon Jeruk","short_name":"Kebon Jeruk","types":["administrative_area_level_3","political"]},{"long_name":"West Jakarta City","short_name":"West Jakarta City","types":["administrative_area_level_2","political"]},{"long_name":"Jakarta","short_name":"Jakarta","types":["administrative_area_level_1","political"]},{"long_name":"印度尼西亚","short_name":"ID","types":["country","political"]}],"formatted_address":"West Jakarta, Kebon Jeruk, West Jakarta City, Jakarta, 印度尼西亚","geometry":{"bounds":{"northeast":{"lat":-6.0951241,"lng":106.828308},"southwest":{"lat":-6.225213099999999,"lng":106.686211}},"location":{"lat":-6.1683295,"lng":106.7588494},"location_type":"APPROXIMATE","viewport":{"northeast":{"lat":-6.0951241,"lng":106.828308},"southwest":{"lat":-6.225213099999999,"lng":106.686211}}},"place_id":"ChIJGdcQ0cn3aS4RoLjULejFAAM","types":["locality","political"]},{"address_components":[{"long_name":"雅加达","short_name":"雅加达","types":["colloquial_area","locality","political"]},{"long_name":"大雅加达","short_name":"大雅加达","types":["administrative_area_level_1","political"]},{"long_name":"印度尼西亚","short_name":"ID","types":["country","political"]}],"formatted_address":"印度尼西亚大雅加达雅加达","geometry":{"bounds":{"northeast":{"lat":-6.0886599,"lng":106.972825},"southwest":{"lat":-6.3708331,"lng":106.686211}},"location":{"lat":-6.2087634,"lng":106.845599},"location_type":"APPROXIMATE","viewport":{"northeast":{"lat":-6.0886599,"lng":106.972825},"southwest":{"lat":-6.3708331,"lng":106.686211}}},"place_id":"ChIJnUvjRenzaS4RoobX2g-_cVM","types":["colloquial_area","locality","political"]},{"address_components":[{"long_name":"7","short_name":"7","types":["political"]},{"long_name":"","short_name":"","types":["political"]},{"long_name":"Tomang","short_name":"Tomang","types":["administrative_area_level_4","political"]},{"long_name":"Grogol petamburan","short_name":"Grogol petamburan","types":["administrative_area_level_3","political"]},{"long_name":"Kota Jakarta Barat","short_name":"Kota Jakarta Barat","types":["administrative_area_level_2","political"]},{"long_name":"Daerah Khusus Ibukota Jakarta","short_name":"Daerah Khusus Ibukota Jakarta","types":["administrative_area_level_1","political"]},{"long_name":"印度尼西亚","short_name":"ID","types":["country","political"]}],"formatted_address":"RT.7, Tomang, Grogol petamburan, Kota Jakarta Barat, Daerah Khusus Ibukota Jakarta, 印度尼西亚","geometry":{"bounds":{"northeast":{"lat":-6.1689399,"lng":106.8002599},"southwest":{"lat":-6.170142999999999,"lng":106.799724}},"location":{"lat":-6.169315699999999,"lng":106.8000474},"location_type":"APPROXIMATE","viewport":{"northeast":{"lat":-6.168192469708497,"lng":106.8013409302915},"southwest":{"lat":-6.170890430291502,"lng":106.7986429697085}}},"place_id":"ChIJ9SLXf2b2aS4R_V7r5OrOknU","types":["political"]},{"address_components":[{"long_name":"10","short_name":"10","types":["political"]},{"long_name":"Tomang","short_name":"Tomang","types":["administrative_area_level_4","political"]},{"long_name":"Grogol petamburan","short_name":"Grogol petamburan","types":["administrative_area_level_3","political"]},{"long_name":"Kota Jakarta Barat","short_name":"Kota Jakarta Barat","types":["administrative_area_level_2","political"]},{"long_name":"Daerah Khusus Ibukota Jakarta","short_name":"Daerah Khusus Ibukota Jakarta","types":["administrative_area_level_1","political"]},{"long_name":"印度尼西亚","short_name":"ID","types":["country","political"]}],"formatted_address":"RW.10, Tomang, Grogol petamburan, Kota Jakarta Barat, Daerah Khusus Ibukota Jakarta, 印度尼西亚","geometry":{"bounds":{"northeast":{"lat":-6.166009799999999,"lng":106.8013469},"southwest":{"lat":-6.170661,"lng":106.796793}},"location":{"lat":-6.1679924,"lng":106.7990335},"location_type":"APPROXIMATE","viewport":{"northeast":{"lat":-6.166009799999999,"lng":106.8013469},"southwest":{"lat":-6.170661,"lng":106.796793}}},"place_id":"ChIJkV2ubGb2aS4RQAXu5qCQc2o","types":["political"]},{"address_components":[{"long_name":"Tomang","short_name":"Tomang","types":["administrative_area_level_4","political"]},{"long_name":"Grogol petamburan","short_name":"Grogol petamburan","types":["administrative_area_level_3","political"]},{"long_name":"West Jakarta City","short_name":"West Jakarta City","types":["administrative_area_level_2","political"]},{"long_name":"Jakarta","short_name":"Jakarta","types":["administrative_area_level_1","political"]},{"long_name":"印度尼西亚","short_name":"ID","types":["country","political"]}],"formatted_address":"Tomang, Grogol petamburan, West Jakarta City, Jakarta, 印度尼西亚","geometry":{"bounds":{"northeast":{"lat":-6.16599,"lng":106.8054711},"southwest":{"lat":-6.1796599,"lng":106.78784}},"location":{"lat":-6.1747751,"lng":106.7999513},"location_type":"APPROXIMATE","viewport":{"northeast":{"lat":-6.1659923,"lng":106.805333},"southwest":{"lat":-6.1796599,"lng":106.78784}}},"place_id":"ChIJk2GG0GD2aS4RIMHj1zziJp8","types":["administrative_area_level_4","political"]},{"address_components":[{"long_name":"11440","short_name":"11440","types":["postal_code"]},{"long_name":"Tomang","short_name":"Tomang","types":["administrative_area_level_4","political"]},{"long_name":"Grogol petamburan","short_name":"Grogol petamburan","types":["administrative_area_level_3","political"]},{"long_name":"West Jakarta City","short_name":"West Jakarta City","types":["administrative_area_level_2","political"]},{"long_name":"Jakarta","short_name":"Jakarta","types":["administrative_area_level_1","political"]},{"long_name":"印度尼西亚","short_name":"ID","types":["country","political"]}],"formatted_address":"Tomang, Grogol petamburan, West Jakarta City, Jakarta 11440印度尼西亚","geometry":{"bounds":{"northeast":{"lat":-6.165930299999999,"lng":106.8057098},"southwest":{"lat":-6.179603999999999,"lng":106.7880935}},"location":{"lat":-6.169647200000001,"lng":106.79719},"location_type":"APPROXIMATE","viewport":{"northeast":{"lat":-6.165930299999999,"lng":106.8057098},"southwest":{"lat":-6.179603999999999,"lng":106.7880935}}},"place_id":"ChIJ01LNgGP2aS4RwFsDTevFABw","types":["postal_code"]},{"address_components":[{"long_name":"Grogol petamburan","short_name":"Grogol petamburan","types":["administrative_area_level_3","political"]},{"long_name":"West Jakarta City","short_name":"West Jakarta City","types":["administrative_area_level_2","political"]},{"long_name":"Jakarta","short_name":"Jakarta","types":["administrative_area_level_1","political"]},{"long_name":"印度尼西亚","short_name":"ID","types":["country","political"]}],"formatted_address":"Grogol petamburan, West Jakarta City, Jakarta, 印度尼西亚","geometry":{"bounds":{"northeast":{"lat":-6.142500999999999,"lng":106.8054711},"southwest":{"lat":-6.185429999999999,"lng":106.7676499}},"location":{"lat":-6.162274999999999,"lng":106.7883416},"location_type":"APPROXIMATE","viewport":{"northeast":{"lat":-6.142591100000001,"lng":106.805333},"southwest":{"lat":-6.1854099,"lng":106.7678605}}},"place_id":"ChIJy43lQ0T2aS4RCoKGEvas3hw","types":["administrative_area_level_3","political"]},{"address_components":[{"long_name":"West Jakarta City","short_name":"West Jakarta City","types":["administrative_area_level_2","political"]},{"long_name":"Jakarta","short_name":"Jakarta","types":["administrative_area_level_1","political"]},{"long_name":"印度尼西亚","short_name":"ID","types":["country","political"]}],"formatted_address":"West Jakarta City, Jakarta, 印度尼西亚","geometry":{"bounds":{"northeast":{"lat":-6.0951241,"lng":106.828308},"southwest":{"lat":-6.225213099999999,"lng":106.686211}},"location":{"lat":-6.1683295,"lng":106.7588494},"location_type":"APPROXIMATE","viewport":{"northeast":{"lat":-6.0951241,"lng":106.828308},"southwest":{"lat":-6.225213099999999,"lng":106.686211}}},"place_id":"ChIJ7x9CSLj3aS4RmvSqYhrWPhg","types":["administrative_area_level_2","political"]},{"address_components":[{"long_name":"大雅加达","short_name":"大雅加达","types":["administrative_area_level_1","political"]},{"long_name":"印度尼西亚","short_name":"ID","types":["country","political"]}],"formatted_address":"印度尼西亚大雅加达","geometry":{"bounds":{"northeast":{"lat":-5.1843219,"lng":106.972825},"southwest":{"lat":-6.3708331,"lng":106.3831259}},"location":{"lat":-6.17511,"lng":106.8650395},"location_type":"APPROXIMATE","viewport":{"northeast":{"lat":-6.0886599,"lng":106.972825},"southwest":{"lat":-6.370829899999999,"lng":106.686211}}},"place_id":"ChIJnUvjRenzaS4RILjULejFAAE","types":["administrative_area_level_1","political"]}]
     * status : OK
     */

    private String status;
    private List<ResultsBean> results;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ResultsBean> getResults() {
        return results;
    }

    public void setResults(List<ResultsBean> results) {
        this.results = results;
    }

    public static class ResultsBean {
        /**
         * address_components : [{"long_name":"248","short_name":"248","types":["street_number"]},{"long_name":"Jalan Tomang Utara II","short_name":"Jl. Tomang Utara II","types":["route"]},{"long_name":"","short_name":"","types":["political"]},{"long_name":"","short_name":"","types":["political"]},{"long_name":"Tomang","short_name":"Tomang","types":["administrative_area_level_4","political"]},{"long_name":"Grogol petamburan","short_name":"Grogol petamburan","types":["administrative_area_level_3","political"]},{"long_name":"Kota Jakarta Barat","short_name":"Kota Jakarta Barat","types":["administrative_area_level_2","political"]},{"long_name":"Daerah Khusus Ibukota Jakarta","short_name":"Daerah Khusus Ibukota Jakarta","types":["administrative_area_level_1","political"]},{"long_name":"印度尼西亚","short_name":"ID","types":["country","political"]},{"long_name":"11440","short_name":"11440","types":["postal_code"]}]
         * formatted_address : Jl. Tomang Utara II No.248, Tomang, Grogol petamburan, Kota Jakarta Barat, Daerah Khusus Ibukota Jakarta 11440印度尼西亚
         * geometry : {"location":{"lat":-6.169963,"lng":106.7998999},"location_type":"ROOFTOP","viewport":{"northeast":{"lat":-6.168614019708498,"lng":106.8012488802915},"southwest":{"lat":-6.171311980291502,"lng":106.7985509197085}}}
         * place_id : ChIJrWe4g2b2aS4RbOkm72qb5x0
         * types : ["street_address"]
         */

        private String formatted_address;
        private GeometryBean                geometry;
        private String                      place_id;
        private List<AddressComponentsBean> address_components;
        private List<String>                types;

        public String getFormatted_address() {
            return formatted_address;
        }

        public void setFormatted_address(String formatted_address) {
            this.formatted_address = formatted_address;
        }

        public GeometryBean getGeometry() {
            return geometry;
        }

        public void setGeometry(GeometryBean geometry) {
            this.geometry = geometry;
        }

        public String getPlace_id() {
            return place_id;
        }

        public void setPlace_id(String place_id) {
            this.place_id = place_id;
        }

        public List<AddressComponentsBean> getAddress_components() {
            return address_components;
        }

        public void setAddress_components(List<AddressComponentsBean> address_components) {
            this.address_components = address_components;
        }

        public List<String> getTypes() {
            return types;
        }

        public void setTypes(List<String> types) {
            this.types = types;
        }

        public static class GeometryBean {
            /**
             * location : {"lat":-6.169963,"lng":106.7998999}
             * location_type : ROOFTOP
             * viewport : {"northeast":{"lat":-6.168614019708498,"lng":106.8012488802915},"southwest":{"lat":-6.171311980291502,"lng":106.7985509197085}}
             */

            private LocationBean location;
            private String       location_type;
            private ViewportBean viewport;

            public LocationBean getLocation() {
                return location;
            }

            public void setLocation(LocationBean location) {
                this.location = location;
            }

            public String getLocation_type() {
                return location_type;
            }

            public void setLocation_type(String location_type) {
                this.location_type = location_type;
            }

            public ViewportBean getViewport() {
                return viewport;
            }

            public void setViewport(ViewportBean viewport) {
                this.viewport = viewport;
            }

            public static class LocationBean {
                /**
                 * lat : -6.169963
                 * lng : 106.7998999
                 */

                private double lat;
                private double lng;

                public double getLat() {
                    return lat;
                }

                public void setLat(double lat) {
                    this.lat = lat;
                }

                public double getLng() {
                    return lng;
                }

                public void setLng(double lng) {
                    this.lng = lng;
                }
            }

            public static class ViewportBean {
                /**
                 * northeast : {"lat":-6.168614019708498,"lng":106.8012488802915}
                 * southwest : {"lat":-6.171311980291502,"lng":106.7985509197085}
                 */

                private NortheastBean northeast;
                private SouthwestBean southwest;

                public NortheastBean getNortheast() {
                    return northeast;
                }

                public void setNortheast(NortheastBean northeast) {
                    this.northeast = northeast;
                }

                public SouthwestBean getSouthwest() {
                    return southwest;
                }

                public void setSouthwest(SouthwestBean southwest) {
                    this.southwest = southwest;
                }

                public static class NortheastBean {
                    /**
                     * lat : -6.168614019708498
                     * lng : 106.8012488802915
                     */

                    private double lat;
                    private double lng;

                    public double getLat() {
                        return lat;
                    }

                    public void setLat(double lat) {
                        this.lat = lat;
                    }

                    public double getLng() {
                        return lng;
                    }

                    public void setLng(double lng) {
                        this.lng = lng;
                    }
                }

                public static class SouthwestBean {
                    /**
                     * lat : -6.171311980291502
                     * lng : 106.7985509197085
                     */

                    private double lat;
                    private double lng;

                    public double getLat() {
                        return lat;
                    }

                    public void setLat(double lat) {
                        this.lat = lat;
                    }

                    public double getLng() {
                        return lng;
                    }

                    public void setLng(double lng) {
                        this.lng = lng;
                    }
                }
            }
        }

        public static class AddressComponentsBean {
            /**
             * long_name : 248
             * short_name : 248
             * types : ["street_number"]
             */

            private String long_name;
            private String       short_name;
            private List<String> types;

            public String getLong_name() {
                return long_name;
            }

            public void setLong_name(String long_name) {
                this.long_name = long_name;
            }

            public String getShort_name() {
                return short_name;
            }

            public void setShort_name(String short_name) {
                this.short_name = short_name;
            }

            public List<String> getTypes() {
                return types;
            }

            public void setTypes(List<String> types) {
                this.types = types;
            }
        }
    }

}
