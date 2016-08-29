
package br.gov.mec.aghu.patrimonio.action;

import java.io.Serializable;
import java.util.List;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

import br.gov.mec.aghu.patrimonio.vo.DevolucaoBemPermanenteVO;

public class RegistrarDevolucaoBemPermanenteDataModel extends ListDataModel<DevolucaoBemPermanenteVO> implements SelectableDataModel<DevolucaoBemPermanenteVO>, Serializable {

	
    /**
	 * 
	 */
	private static final long serialVersionUID = -9031057105009538311L;

	/**
     * 
     * Construtor da classe.
     */
    public RegistrarDevolucaoBemPermanenteDataModel() {
        super();
    }

    /**
     * 
     * Construtor da classe.
     * 
     * @param data {@link List} de {@link }
     */
    public RegistrarDevolucaoBemPermanenteDataModel(List<DevolucaoBemPermanenteVO> lista) {
        super(lista);
    }

    /**
     * 
     * {@inheritDoc}
     * 
     * @see org.primefaces.model.SelectableDataModel#getRowData(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    public DevolucaoBemPermanenteVO getRowData(String rowKey) {

        List<DevolucaoBemPermanenteVO> listaDevBemPerman = (List<DevolucaoBemPermanenteVO>) getWrappedData();

        for (DevolucaoBemPermanenteVO devBemPerman : listaDevBemPerman) {
            if (devBemPerman.getPbpSeq().equals(rowKey)){
                return devBemPerman;
            }
        }

        return null;
    }

    /**
     * 
     * {@inheritDoc}
     * 
     * @see org.primefaces.model.SelectableDataModel#getRowKey(java.lang.Object)
     */
    @Override
    public Object getRowKey(DevolucaoBemPermanenteVO devBemPerman) {
        return devBemPerman.getPbpSeq();
    }
}
