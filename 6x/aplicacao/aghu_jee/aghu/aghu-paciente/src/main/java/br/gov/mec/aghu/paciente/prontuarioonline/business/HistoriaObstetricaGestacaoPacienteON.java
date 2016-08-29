package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.model.MbcFichaAnestesias;
import br.gov.mec.aghu.model.McoGestacoes;
import br.gov.mec.aghu.model.McoLogImpressoes;
import br.gov.mec.aghu.model.McoRecemNascidos;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.HistObstetricaVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.NodoPOLVO;
import br.gov.mec.aghu.perinatologia.business.IPerinatologiaFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * História 17320 
 */ 
@Stateless
public class HistoriaObstetricaGestacaoPacienteON extends BaseBusiness {


@EJB
private InformacoesPerinataisON informacoesPerinataisON;

private static final Log LOG = LogFactory.getLog(HistoriaObstetricaGestacaoPacienteON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IPerinatologiaFacade perinatologiaFacade;

@EJB
private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	private static final long serialVersionUID = -1302450216068743132L;

	public Boolean habilitarBotaoConsObs (HistObstetricaVO registroSelecionado){
		if (DominioOrigemAtendimento.I.equals(registroSelecionado.getOrigemAtendimento())){
			return false;
		}
		
		List<McoLogImpressoes> consConsulta = getPerinatologiaFacade().pesquisarLogImpressoesEventoMcorConsultaObs(registroSelecionado.getGsoPacCodigo(), 
				registroSelecionado.getGsoSeqp(), 
				registroSelecionado.getConNumero());
		if (consConsulta == null || consConsulta.isEmpty()){
			return false;
		}
		
		return true;
	}

	public Boolean habilitarBotaoAdmObs(DominioOrigemAtendimento origemAtedimento, Integer pacCodigo,
			Short gsoSeqp, Integer conNumero) {
		if (DominioOrigemAtendimento.A.equals(origemAtedimento)){
			return false;
		}
		
		List<McoLogImpressoes> consAdmissao = getPerinatologiaFacade().pesquisarLogImpressoesEventoMcorAdmissaoObs(pacCodigo,gsoSeqp,conNumero);
		if (consAdmissao == null || consAdmissao.isEmpty()){
			return false;
		}
		
		return true;
	}	
	
	public Boolean habilitarBotaoPartoComRestricaoDeOrigem(
			DominioOrigemAtendimento origemAtedimento, Integer pacCodigo,
			Short gsoSeqp, DominioOrigemAtendimento... origensRestritas) {
		
		if(Arrays.asList(origensRestritas).contains(origemAtedimento)){
			return false;
		}
		
		return getInformacoesPerinataisON().habilitarBotaoParto(pacCodigo, gsoSeqp);
	}	
	
	private InformacoesPerinataisON getInformacoesPerinataisON(){
		return informacoesPerinataisON;
	}
	
	protected List<MbcFichaAnestesias> qFic(Integer atdSeq, Integer gsoPacCodigo, Short gsoSeqp) {
		return getBlocoCirurgicoFacade().pesquisarMbcFichaAnestesiasAtdGso(atdSeq,gsoPacCodigo,gsoSeqp); 
	}
	
	public Boolean habilitarBotaoAtoAnestesicoProc(HistObstetricaVO registroSelecionado){
		
		if (DominioOrigemAtendimento.I.equals(registroSelecionado.getOrigemAtendimento())){
			List<MbcFichaAnestesias> fic = qFic(registroSelecionado.getSeqAtendimento(), 
					registroSelecionado.getGsoPacCodigo(), 
					registroSelecionado.getGsoSeqp());
			if (fic != null && !fic.isEmpty()){
				return true;
			}else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public Boolean habilitarBotaoNascimentoComRestricaoDeOrigem(
			DominioOrigemAtendimento origemAtedimento, Integer pacCodigo,
			Short gsoSeqp, DominioOrigemAtendimento... origensRestritas) {
		
		if(Arrays.asList(origensRestritas).contains(origemAtedimento)){
			return false;
		}
		
		List<McoRecemNascidos> rna = getPerinatologiaFacade().pesquisarRecemNascidosPorCodigoPacienteSeqp(pacCodigo, gsoSeqp);
		if (rna == null || rna.isEmpty()){
			return false;
		}
		
		return getInformacoesPerinataisON().habilitarBotaoNascimento(pacCodigo, gsoSeqp);
	}	

	protected IPerinatologiaFacade getPerinatologiaFacade() {
		return perinatologiaFacade;
	}
	
	private IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}
	
	protected void carregaNosGestacao(NodoPOLVO no, Integer gsoPacCodigo) {
		List<McoGestacoes> gestacoes = getPerinatologiaFacade().pesquisarMcoGestacoes(gsoPacCodigo);  
		for (McoGestacoes mcoGestacoes : gestacoes) {
			Short gsoSeqp = mcoGestacoes.getId().getSeqp();
			String descricao = "Gestação " + gsoSeqp.toString();
			String tipo = "folhaGestacao"; 
			String pagina = "/paciente/prontuarioonline/historiaObstetricaPorGestacaoPacienteListPOL.xhtml";
			
			NodoPOLVO folha = new NodoPOLVO(gsoPacCodigo,tipo,descricao,pagina, "/resources/img/icons/gestacao.png");
			folha.addParam("seqGestacoes", gsoSeqp);
			no.getNodos().add(folha); 
		}
	}

	
}
