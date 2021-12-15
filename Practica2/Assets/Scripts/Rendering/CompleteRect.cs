using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class CompleteRect : MonoBehaviour
{
    [SerializeField] GameObject children;
    public void Close()
    {
        children.SetActive(false);
    }

    public void Open()
    {
        children.SetActive(true);
    }
}
