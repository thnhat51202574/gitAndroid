package Modules;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nhatth  on 12/22/16.
 */

public class MDistance {

    // Calculator Map Point
    // http://stackoverflow.com/questions/27928/calculate-distance-between-two-latitude-longitude-points-haversine-formula
    static private double calDistanceBetweenTwoNode(LatLng node_first,
                                             LatLng node_second) {
        double dLat = (Math.PI / 180) * (node_first.latitude - node_second.latitude);
        double dLon = (Math.PI / 180) * (node_first.longitude - node_second.longitude);
        double a = Math.pow(Math.sin(dLat / 2), 2)
                + Math.cos((Math.PI / 180) * node_first.latitude)
                * Math.cos((Math.PI / 180) * node_second.latitude)
                * Math.pow(Math.sin(dLon / 2), 2);
        return 6371 * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)) * 1000;
    }

    static public Double minDistance(List<LatLng> allNode,
                               LatLng nodeCurrent) {
        double h_cur_prev = 0;
        double h_cur_next = 0;
        double h = 0;
        int size = allNode.size();
        if (size >= 2) {
            double min_distance = 0;
            int index_distance_min = 0;
            for (int i = 0; i < size; i++) {
                // distance
                double distance = MDistance.calDistanceBetweenTwoNode(nodeCurrent,
                        allNode.get(i));
                if (distance <= min_distance && min_distance > 0
                        || min_distance == 0) {
                    index_distance_min = i;
                    min_distance = distance;
                }
            }
            // distance previous node
            if(index_distance_min > 0) {
                double dis_cur_to_prev = 0;
                if (index_distance_min > 0) {
                    dis_cur_to_prev = MDistance.calDistanceBetweenTwoNode(nodeCurrent,
                            allNode.get(index_distance_min - 1));
                }

                double dis_cur_prev = MDistance.calDistanceBetweenTwoNode(
                        allNode.get(index_distance_min),
                        allNode.get(index_distance_min - 1));

                // calculator height
                double p_cur_prev = (dis_cur_to_prev + dis_cur_prev + min_distance) / 2;
                h_cur_prev = 2
                        * Math.sqrt(p_cur_prev * (p_cur_prev - min_distance) * (p_cur_prev - dis_cur_to_prev)
                        * (p_cur_prev - dis_cur_prev)) / dis_cur_to_prev;
            }
            if(index_distance_min < size - 1) {
                // distance next node
                double dis_cur_to_next = 0;
                if (index_distance_min < size - 1) {
                    dis_cur_to_next = MDistance.calDistanceBetweenTwoNode(nodeCurrent,
                            allNode.get(index_distance_min + 1));
                }
                double dis_cur_next = MDistance.calDistanceBetweenTwoNode(
                        allNode.get(index_distance_min),
                        allNode.get(index_distance_min + 1));

                // calculator height
                double p_cur_next = (dis_cur_to_next + dis_cur_next + min_distance) / 2;
                h_cur_next = 2
                        * Math.sqrt(p_cur_next * (p_cur_next - min_distance) * (p_cur_next - dis_cur_next)
                        * (p_cur_next - dis_cur_to_next)) / dis_cur_next;
            }
            h = (h_cur_next > h_cur_prev)? h_cur_next:h_cur_prev;
        }
        return h;
    }


}