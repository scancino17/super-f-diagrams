/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model;

/**
 *
 * @author sebca
 */
class Union implements Element{

    @Override
    public String getName() {
        //Acá no debería retornar nada?
        //El tema es que lo único que necesitan para ser Element es tener un
        //método que retorne un nombre.
        //Hay que mejorar el "contrato", creo yo.
        // - Seba
        return null;
    }
    
}
