package br.gov.mec.aghu.exames.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelAmostraItemExamesDAO;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * ORADB PROCEDURE AELP_TRATA_PROT_UNICO
 * @author aghu
 *
 */
@Stateless
public class TratarProtocoloUnicoRN extends BaseBusiness{


@EJB
private GerarProtocoloUnicoRN gerarProtocoloUnicoRN;

@EJB
private AelAmostraItemExamesRN aelAmostraItemExamesRN;

private static final Log LOG = LogFactory.getLog(TratarProtocoloUnicoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelAmostraItemExamesDAO aelAmostraItemExamesDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 8889578679006114585L;

	/**
	 * ORADB PROCEDURE AELP_TRATA_PROT_UNICO
	 * @param amostra
	 */
	public void tratarProtocoloUnico(AelAmostras amostra, boolean cancelaInterfaceamento, String nomeMicrocomputador) throws BaseException{
		
		// Chamada da PROCEDURE AELP_GERA_PROT_UNIC
		this.getGerarProtocoloUnicoRN().gerarProtocoloUnico(amostra, cancelaInterfaceamento, null); // TODO DESCOMENTAR

		// Pesquisa amostras RECEBIDAS com equipamentos contendo CARGA AUTOMATICA
		List<AelAmostraItemExames> listaAmostaItemExames = this.getAelAmostraItemExamesDAO().pesquisarAmostraItemExamesTratarProtocoloUnico(amostra.getSolicitacaoExame().getSeq(), amostra.getId().getSeqp().intValue());
		
		for (AelAmostraItemExames amostraItemExame : listaAmostaItemExames) {
			
			// Atualiza a Amostra Item Exame com a situaçao modificada para enviado
			if(cancelaInterfaceamento){
				amostraItemExame.setIndEnviado(false);
			}else{
				amostraItemExame.setIndEnviado(true);
			}
			
			this.getAelAmostraItemExamesRN().atualizarAelAmostraItemExames(amostraItemExame, true, false, nomeMicrocomputador);
		}
		
	}
	
	/*
	 * Getters para dependências
	 */
	
	protected AelAmostraItemExamesDAO getAelAmostraItemExamesDAO() {
		return aelAmostraItemExamesDAO;
	}
	
	protected AelAmostraItemExamesRN getAelAmostraItemExamesRN() {
		return aelAmostraItemExamesRN;
	}
	
	protected GerarProtocoloUnicoRN getGerarProtocoloUnicoRN() {
		return gerarProtocoloUnicoRN;
	}

}
