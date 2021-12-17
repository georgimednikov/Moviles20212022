using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

[RequireComponent(typeof(Button))]
public class DeactivateButton : MonoBehaviour
{
    [SerializeField] int limit;

    private void Start()
    {
        GetComponent<Button>().interactable &= limit > 1;
    }

    public void Deactivate()
    {
        GetComponent<Button>().interactable = limit-- > 1;
    }

    public void SetLimit(int limit)
    {
        this.limit = limit;
    }

    public void OppositeClicked()
    {
        GetComponent<Button>().interactable = ++limit > 1;
    }
}
