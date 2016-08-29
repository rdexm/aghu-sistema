package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioIndConcluido;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.McoLogImpressoes;
import br.gov.mec.aghu.paciente.prontuario.vo.AltaObitoSumarioVO;
import br.gov.mec.aghu.paciente.prontuario.vo.InformacoesPerinataisVO;
import br.gov.mec.aghu.perinatologia.business.IPerinatologiaFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * História 21183
 */ 
@Stateless
public class InformacoesPerinataisON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(InformacoesPerinataisON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IPrescricaoMedicaFacade prescricaoMedicaFacade;

@EJB
private IPerinatologiaFacade perinatologiaFacade;

	private static final long serialVersionUID = -1302450216068743132L;
	
	public Boolean habilitarBotaoParto (Integer pacCodigo,	Short gsoSeqp){
		List<McoLogImpressoes> consConsulta = getPerinatologiaFacade().	pesquisarLogImpressoesEventoMcorNascimento(pacCodigo, gsoSeqp);
		if (consConsulta == null || consConsulta.isEmpty()){
			return false;
		}
		return true;		
	}

	public Boolean habilitarBotaoNascimento(Integer pacCodigo,Short gsoSeqp){
		List<McoLogImpressoes> consConsulta = getPerinatologiaFacade().
			pesquisarLogImpressoesEventoMcorRnSlParto(pacCodigo, gsoSeqp);
		if (consConsulta == null || consConsulta.isEmpty()){
			return false;
		}
		return true;
	}
	
	public Boolean habilitarBotaoExameFisico(
			DominioOrigemAtendimento tipoOrigemAtendimento,
			Integer mcoGestacoesPacCodigo, Short mcoGestacoesSeqP, DominioOrigemAtendimento ...origensRestritas) {
		if(mcoGestacoesPacCodigo == null || mcoGestacoesSeqP == null){//São nulso quando não tem nenhum registro selecionado(entrada da tela)
			return false;
		}
		
		if(tipoOrigemAtendimento != null && Arrays.asList(origensRestritas).contains(tipoOrigemAtendimento)){
			return false;
		}
		
		List<McoLogImpressoes> consConsulta = getPerinatologiaFacade().
		pesquisarLogImpressoesEventoMcorExFisicoRn(mcoGestacoesPacCodigo, mcoGestacoesSeqP);
		if (consConsulta == null || consConsulta.isEmpty()){
			return false;
		}
		return true;
	}
	
	public Boolean habilitarBotaoSumarioAlta(InformacoesPerinataisVO registroSelecionado) {
		if (DominioOrigemAtendimento.A.equals(registroSelecionado.getTipo())){
			return false;
		}
			
		List<AltaObitoSumarioVO> consConsulta = getPrescricaoMedicaFacade().pesquisarAltaObitoSumariosAtdSeqSituacaoIndConcluido
		(registroSelecionado.getAtdSeq(), DominioSituacao.A, DominioIndConcluido.S);
		if (consConsulta == null || consConsulta.isEmpty()){
			return false;
		}
		
		return true;
	}
	
	protected IPerinatologiaFacade getPerinatologiaFacade() {
		return perinatologiaFacade;
	}
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	public List<InformacoesPerinataisVO> pesquisarInformacoesPerinataisCodigoPaciente(Integer pacCodigo, Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc){		

		List<InformacoesPerinataisVO> perinataisVOs = getPerinatologiaFacade().pesquisarInformacoesPerinataisCodigoPaciente(pacCodigo);
		
		if(firstResult != null && maxResult != null) {
			// A paginação é feita de forma manual, eliminando elementos que não
			// devem aparecer no resultado da pesquisa. 10 é o tamanho máximo de
			// itens por página.
			if (perinataisVOs.size() > 10 && firstResult < perinataisVOs.size()) {
				int toIndex = firstResult + maxResult;
	
				if (toIndex > perinataisVOs.size()) {
					toIndex = perinataisVOs.size();
				}
	
				perinataisVOs = perinataisVOs.subList(firstResult, toIndex);
			}
		}		
		return perinataisVOs;
	}

	public Integer pesquisarInformacoesPerinataisCodigoPacienteCount(
			Integer pacCodigo) {
		return getPerinatologiaFacade().pesquisarInformacoesPerinataisCodigoPaciente(pacCodigo).size();
	}
}
