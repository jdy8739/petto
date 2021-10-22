import { 
  FETCH_USER_INFO, 
  FETCH_SESSION,
  FETCH_FACILITY_LIST 

} from "./mutation-types";

import axios from "axios";
// import router from '../router'
import cookies from "vue-cookies";

export default {
  async TodayPopUp() {
    if (cookies.isKey("TodayPopUpClose") == false) {
      cookies.set("TodayPopUpClose", "todayClose", "1d");
      console.log(cookies.get("TodayPopUpClose"));
    }
  },

  fetchUserInfo({ commit }, payload) {
    return axios.get(`http://localhost:8888/petto/member/getUserInfo/${payload}`)
      .then(res => {
        commit(FETCH_USER_INFO, res.data);
      });
  },

  fetchFacilityList({ commit }) {
    return axios.get('http://localhost:8888/petto/facility/getFacilityList')
      .then(res => {
        commit(FETCH_FACILITY_LIST, res.data);
      });
  },
  // Session
  fetchSession({ commit }) {
    commit(FETCH_SESSION);
  },
};
