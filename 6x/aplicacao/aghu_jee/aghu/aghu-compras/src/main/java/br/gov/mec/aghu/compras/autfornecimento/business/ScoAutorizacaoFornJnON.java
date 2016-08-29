package br.gov.mec.aghu.compras.autfornecimento.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.autfornecimento.vo.ResponsavelAfVO;
import br.gov.mec.aghu.compras.dao.ScoAutorizacaoFornJnDAO;
import br.gov.mec.aghu.model.ScoAutorizacaoFornJn;
import br.gov.mec.aghu.model.ScoMotivoAlteracaoAf;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * Objeto de negócio responsável por journaling de AF.
 * 
 * @author mlcruz
 *
 */
@Stateless
public class ScoAutorizacaoFornJnON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ScoAutorizacaoFornJnON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private ScoAutorizacaoFornJnDAO scoAutorizacaoFornJnDAO;
	private static final long serialVersionUID = 7276860329724573921L;
	
	/**
	 * Obtem responsáveis a partir da versão de uma AF.
	 * 
	 * @param numeroAf
	 * @param complementoAf
	 * @param sequenciaAlteracao
	 * @return Responsáveis
	 */
	public List<ResponsavelAfVO> obterResponsaveisAutorizacaoFornJn(
			Integer numeroAf, Short complementoAf, Short sequenciaAlteracao) {
		ScoAutorizacaoFornJn afJn = getScoAutorizacaoFornJnDAO()
				.obterResponsaveisAutorizacaoFornJn(numeroAf, complementoAf, sequenciaAlteracao);
		
		List<ResponsavelAfVO> responsaveis = new ArrayList<ResponsavelAfVO>();
		
		ResponsavelAfVO gerador = new ResponsavelAfVO();
		gerador.setOperacao(getResourceBundleValue("LABEL_SERVIDOR_GERACAO_AF"));
		gerador.setUsuario(afJn.getServidor());
		gerador.setData(afJn.getDtGeracao());
		responsaveis.add(gerador);
		
		ResponsavelAfVO controlado = new ResponsavelAfVO();
		controlado.setOperacao(getResourceBundleValue("LABEL_SERVIDOR_CONTROLADO_AF"));
		controlado.setUsuario(afJn.getServidorControlado());
		controlado.setData(afJn.getDtAlteracao());
		responsaveis.add(controlado);
		
		ResponsavelAfVO exclusao = new ResponsavelAfVO();
		exclusao.setOperacao(getResourceBundleValue("LABEL_SERVIDOR_EXCLUSAO_AF"));
		exclusao.setUsuario(afJn.getServidorExcluido());
		exclusao.setData(afJn.getDtExclusao());
		responsaveis.add(exclusao);
		
		ResponsavelAfVO estorno = new ResponsavelAfVO();
		estorno.setOperacao(getResourceBundleValue("LABEL_SERVIDOR_ESTORNO_AF"));
		estorno.setUsuario(afJn.getServidorEstorno());
		estorno.setData(afJn.getDtEstorno());
		responsaveis.add(estorno);
		
		ResponsavelAfVO assinatura = new ResponsavelAfVO();
		assinatura.setOperacao(getResourceBundleValue("LABEL_SERVIDOR_ASSINATURA_AF"));
		assinatura.setUsuario(afJn.getServidorAssinaCoord());
		assinatura.setData(afJn.getDtAssinaturaCoord());
		responsaveis.add(assinatura);
		
		ResponsavelAfVO chefia = new ResponsavelAfVO();
		chefia.setOperacao(getResourceBundleValue("LABEL_SERVIDOR_CHEFIA_AF"));
		chefia.setUsuario(afJn.getServidorAutorizado());
		chefia.setData(afJn.getDtAssinaturaChefia());
		responsaveis.add(chefia);
		
		return responsaveis;
	}
	
	public ScoMotivoAlteracaoAf buscarUltimoMotivoAlteracao(Integer numAf, Integer numPac, Short nroComplemento){
		
		Short maxSeqAlteracao = this.getScoAutorizacaoFornJnDAO().obterMaxSequenciaAlteracaoAfJn(numAf);
		if (maxSeqAlteracao!=null){
			ScoAutorizacaoFornJn  afJn = this.getScoAutorizacaoFornJnDAO().obterScoAutorizacaoFornJn(numPac, nroComplemento, maxSeqAlteracao);
			if (afJn!=null && afJn.getNumero()!=null){
				return afJn.getMotivoAlteracaoAf();
			}
			
		}
		return null;
	}
	
	// Dependências

	protected ScoAutorizacaoFornJnDAO getScoAutorizacaoFornJnDAO() {
		return scoAutorizacaoFornJnDAO;
	}
}
