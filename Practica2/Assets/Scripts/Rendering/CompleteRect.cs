using System.Collections;
using System.Collections.Generic;
using UnityEngine;

/// <summary>
/// Clase encargada de controlar el popup de cuando se termina un nivel
/// </summary>
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
