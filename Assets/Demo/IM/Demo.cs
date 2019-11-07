using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class Demo : MonoBehaviour {

    TimSDK timsdk;

    public Button[] buttons;
    public InputField[] inputs;
    public Text content;

    void Start() {
        timsdk = this.GetComponent<TimSDK>();
        //初始化SDK
        timsdk.Init();

        AddListener();
    }

    public void AddListener()
    {
        timsdk.MessageCallback += (data) =>
            {
                content.text = data;
            };

        buttons[0].onClick.AddListener(() =>
        {
            timsdk.Login(inputs[0].text);
        });
        buttons[1].onClick.AddListener(() =>
        {
            timsdk.GetFriendsList();
        });
        buttons[2].onClick.AddListener(() =>
        {
            timsdk.AddFriend(inputs[2].text, "addword","备注");
        });
        buttons[3].onClick.AddListener(() =>
        {
            timsdk.GetMessages(inputs[3].text, 10, 1);
        });
        buttons[4].onClick.AddListener(() =>
        {
            timsdk.SendMessage(inputs[4].text, inputs[5].text, 1);
        });
        buttons[5].onClick.AddListener(() =>
        {
            timsdk.GetPendencyList();
        });
        buttons[6].onClick.AddListener(() =>
        {
            timsdk.GetUsersProfile(inputs[6].text,true);
        });
        buttons[7].onClick.AddListener(() =>
        {
            timsdk.CreateGroup(2);
        });
        buttons[8].onClick.AddListener(() =>
        {
            timsdk.ApplyJoinGroup(inputs[8].text,"reason");
        });
        buttons[9].onClick.AddListener(() =>
        {
            timsdk.GetGroupMembers(inputs[9].text);
        });
        buttons[10].onClick.AddListener(() =>
        {
            timsdk.GetGroupList();
        });
        buttons[11].onClick.AddListener(() =>
        {
            timsdk.QuitGroup(inputs[11].text);
        });
        buttons[12].onClick.AddListener(() =>
        {
            timsdk.ModifySelfProfile(inputs[12].text,1,20190419,"个性签名", "AllowType_Type_NeedConfirm");
        });
    }
}
