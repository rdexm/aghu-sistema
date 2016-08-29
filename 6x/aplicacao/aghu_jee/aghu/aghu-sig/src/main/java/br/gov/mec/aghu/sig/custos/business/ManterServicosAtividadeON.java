package br.gov.mec.aghu.sig.custos.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.SigAtividadeServicos;
import br.gov.mec.aghu.sig.dao.SigAtividadeServicosDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterServicosAtividadeON extends BaseBusiness{

private static final Log LOG = LogFactory.getLog(ManterServicosAtividadeON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private SigAtividadeServicosDAO sigAtividadeServicosDAO;

	private static final long serialVersionUID = 5588659390220573813L;


	public enum ManterServicosAtividadeONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ITEM_CONTRATO_JA_ASSOCIADO_ATIVIDADE,
		MENSAGEM_SIG_DIRECIONADORES_OBRIGATORIO,
		MENSAGEM_ITEM_CONTRATO_OBRIGATORIO,
		MENSAGEM_AUTORIZ_FORNEC_ASSOCIADO_ATIVIDADE;
	}
	
	public void validarInclusaoServicosAtividade(SigAtividadeServicos servico, List<SigAtividadeServicos> listaServicos) throws ApplicationBusinessException {
		validarItemContratoManual(servico, listaServicos);
	}
	
	public void validarItemContratoManual(SigAtividadeServicos servico, List<SigAtividadeServicos> listaServicos) throws ApplicationBusinessException{
		if (servico.getSigDirecionadores() == null){
			throw new ApplicationBusinessException(ManterServicosAtividadeONExceptionCode.MENSAGEM_SIG_DIRECIONADORES_OBRIGATORIO);
		}
		for(SigAtividadeServicos itemLista : listaServicos){
			if(servico.getScoItensContrato() != null){
				if(itemLista.getScoItensContrato() != null){
					if(itemLista.getScoItensContrato().getContrato().getSeq().intValue() == servico.getScoItensContrato().getContrato().getSeq().intValue()
						&& itemLista.getScoItensContrato().getSeq().intValue() == servico.getScoItensContrato().getSeq().intValue()){
					throw new ApplicationBusinessException(ManterServicosAtividadeONExceptionCode.MENSAGEM_ITEM_CONTRATO_JA_ASSOCIADO_ATIVIDADE);
					}
				}
			}else{
				if(itemLista.getScoAfContrato() != null &&
				   servico.getScoAfContrato() != null &&
				   servico.getScoAfContrato().getScoContrato() != null){
					if(itemLista.getScoAfContrato().getScoContrato().getSeq().intValue() == servico.getScoAfContrato().getScoContrato().getSeq().intValue()
						&& itemLista.getScoAfContrato() != null && itemLista.getScoAfContrato().getSeq().intValue() == servico.getScoAfContrato().getSeq().intValue()){
					throw new ApplicationBusinessException(ManterServicosAtividadeONExceptionCode.MENSAGEM_ITEM_CONTRATO_JA_ASSOCIADO_ATIVIDADE);
					}
				}else{
					if(itemLista.getAutorizacaoForn() != null &&  servico.getAutorizacaoForn() != null && itemLista.getAutorizacaoForn().getNumero().intValue() == servico.getAutorizacaoForn().getNumero().intValue()
					   && itemLista.getServico()!= null && itemLista.getServico().getCodigo().intValue() == servico.getServico().getCodigo().intValue()){
						throw new ApplicationBusinessException(ManterServicosAtividadeONExceptionCode.MENSAGEM_AUTORIZ_FORNEC_ASSOCIADO_ATIVIDADE);
					}
				}
			}
		}
	}
	
	public void persistirServicos(SigAtividadeServicos sigAtividadeServicos){
		if(sigAtividadeServicos.getSeq() != null){
			this.getSigAtividadeServicosDAO().atualizar(sigAtividadeServicos);
		}else {
			this.getSigAtividadeServicosDAO().persistir(sigAtividadeServicos);
		}
	}
	
	
	protected SigAtividadeServicosDAO getSigAtividadeServicosDAO() {
		return sigAtividadeServicosDAO;
	}
}
