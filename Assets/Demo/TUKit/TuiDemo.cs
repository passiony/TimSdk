using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class TuiDemo : MonoBehaviour {

    TimSDK timsdk;

    void Start() {
        timsdk = this.GetComponent<TimSDK>();
        //初始化SDK
        timsdk.Init();
    }
        //timsdk.MessageCallback += (data) =>
        //{
        //    content.text = data;
        //};

        //buttons[0].onClick.AddListener(() =>
        //{
        //    timsdk.Init();
        //});
        //buttons[1].onClick.AddListener(() =>
        //{
        //    timsdk.Login(inputs[0].text);
        //});
        //buttons[2].onClick.AddListener(() =>
        //{
        //    timsdk.GetFriendsList();
        //});
        //buttons[3].onClick.AddListener(() =>
        //{
        //    timsdk.AddFriend(inputs[1].text,"hello");
        //});
        //buttons[4].onClick.AddListener(() =>
        //{
        //    timsdk.GetMessages(inputs[2].text,10);
        //});
        //buttons[5].onClick.AddListener(() =>
        //{
        //    timsdk.SendMessage(inputs[3].text, inputs[4].text);
        //});
        //buttons[6].onClick.AddListener(() =>
        //{
        //    timsdk.GetUsersProfile(inputs[5].text);
        //});
        //buttons[7].onClick.AddListener(() =>
        //{
        //    timsdk.GetPendencyList();
        //});

}
