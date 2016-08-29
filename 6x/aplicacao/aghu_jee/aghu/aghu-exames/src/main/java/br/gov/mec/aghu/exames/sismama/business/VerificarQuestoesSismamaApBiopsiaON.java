package br.gov.mec.aghu.exames.sismama.business;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.dominio.DominioDeteccaoLesao;
import br.gov.mec.aghu.dominio.DominioDiagnosticoImagem;
import br.gov.mec.aghu.dominio.DominioLocalizacaoMamografia;
import br.gov.mec.aghu.dominio.DominioMamaLesao;
import br.gov.mec.aghu.dominio.DominioSismamaHistoCadCodigo;
import br.gov.mec.aghu.dominio.DominioSismamaProcedimentoCirurgico;
import br.gov.mec.aghu.dominio.DominioSismamaSimNao;
import br.gov.mec.aghu.dominio.DominioSismamaSimNaoNaoSabe;
import br.gov.mec.aghu.dominio.DominioTamanhoLesao;
import br.gov.mec.aghu.dominio.DominioTipoExameHistopatologico;
import br.gov.mec.aghu.exames.dao.AelItemSolicExameHistDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelSismamaHistoCadDAO;
import br.gov.mec.aghu.exames.dao.AelSismamaHistoResDAO;
import br.gov.mec.aghu.exames.dao.AelSismamaHistoResHistDAO;
import br.gov.mec.aghu.model.AelItemSolicExameHist;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSismamaHistoCad;
import br.gov.mec.aghu.model.AelSismamaHistoRes;
import br.gov.mec.aghu.model.AelSismamaHistoResHist;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.Dominio;
import br.gov.mec.aghu.core.dominio.DominioString;

@Stateless
public class VerificarQuestoesSismamaApBiopsiaON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(VerificarQuestoesSismamaApBiopsiaON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelSismamaHistoResDAO aelSismamaHistoResDAO;

@Inject
private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;

@Inject
private AelSismamaHistoCadDAO aelSismamaHistoCadDAO;

@Inject
private AelItemSolicExameHistDAO aelItemSolicExameHistDAO;

@EJB
private IAghuFacade aghuFacade;

@Inject
private AelSismamaHistoResHistDAO aelSismamaHistoResHistDAO;

	private static final long serialVersionUID = 3945848025556021163L;
	

	public Map<String, Object> recuperarRespostas(Integer soeSeq, Short seqp, Boolean isHist) {
		Map<String, Object> mapRespostas = new HashMap<String, Object>();
		
		if(isHist){
			List<AelSismamaHistoResHist> respostas = getAelSismamaHistoResHistDAO().pesquisarSismamaHistoResPorSoeSeqSeqp(soeSeq, seqp);
			for(AelSismamaHistoResHist resposta : respostas) {
				mapRespostas.put(resposta.getSismamaHistoCad().getCodigo(), resposta.getResposta());
			}		
		}else{
			List<AelSismamaHistoRes> respostas = getAelSismamaHistoResDAO().pesquisarSismamaHistoResPorSoeSeqSeqp(soeSeq, seqp);
			for(AelSismamaHistoRes resposta : respostas) {
				mapRespostas.put(resposta.getSismamaHistoCad().getCodigo(), resposta.getResposta());
			}
		}
		this.ajustarRespostas(mapRespostas);
		
		return mapRespostas;
	}
	
	
	public Map<String, Object> recuperarQuestoesRespostas() {		
		Map<String, Object> mapRespostas = new HashMap<String, Object>();		
		List<AelSismamaHistoCad> questoes = getAelSismamaHistoCadDAO().pesquisarSismamaHistoCad();
		
		for(AelSismamaHistoCad questao : questoes) {
			mapRespostas.put(questao.getCodigo(), respostaPadrao(questao.getCodigo()));
		}		
		return mapRespostas;
	}	
	
	
	private Object respostaPadrao(String codigo) {
		if(DominioSismamaHistoCadCodigo.C_CLI_TIPEXE.name().equals(codigo)){
			return DominioTipoExameHistopatologico.BIOPSIA_PECA;
		}else if (DominioSismamaHistoCadCodigo.C_CLI_TCANC.name().equals(codigo)){
			return DominioSismamaSimNaoNaoSabe.NAO_SABE;
		}else if (DominioSismamaHistoCadCodigo.C_CLI_TANT.name().equals(codigo)){
			return DominioSismamaSimNao.SIM;
		}else if (DominioSismamaHistoCadCodigo.C_CLI_GRAV.name().equals(codigo)){
			return DominioSismamaSimNaoNaoSabe.SIM;
		}else if (DominioSismamaHistoCadCodigo.C_CLI_TCIRUR.name().equals(codigo)
				|| DominioSismamaHistoCadCodigo.C_CLI_TCIRUROM.name().equals(codigo)
				|| DominioSismamaHistoCadCodigo.C_CLI_TQUIM.name().equals(codigo)
				|| DominioSismamaHistoCadCodigo.C_CLI_TRADIO.name().equals(codigo)
				|| DominioSismamaHistoCadCodigo.C_CLI_TRADIOOM.name().equals(codigo)					
				|| DominioSismamaHistoCadCodigo.C_CLI_THORM.name().equals(codigo)){						
					
			return Boolean.FALSE;
		} else if (DominioSismamaHistoCadCodigo.C_CLI_LINFO.name().equals(codigo)){
			return DominioSismamaSimNao.SIM;
		} else if (DominioSismamaHistoCadCodigo.C_CLI_MAMA.name().equals(codigo)){
			return DominioMamaLesao.MAMA_DIREITA;
		}
		return null;
	}
	
	
	public void ajustarRespostas(Map<String, Object> respostas) {
		Set<String> setCodigoCampo = respostas.keySet();
		for (String codigoCampo : setCodigoCampo) {
			respostas.put(codigoCampo, obterResposta(codigoCampo, (String)respostas.get(codigoCampo)));
		}
	}
	
	
	public AelSismamaHistoRes criarResposta(AelItemSolicitacaoExames itemSolicitacaoExame, String codigo, Object resposta, RapServidores rap){
		AelSismamaHistoCad questao = getAelSismamaHistoCadDAO().obterPorChavePrimaria(codigo);
		AelSismamaHistoRes res = new AelSismamaHistoRes();		
		res.setItemSolicitacaoExame(itemSolicitacaoExame);
		res.setCriadoEm(new Date());
		res.setResposta(converterResposta(questao.getCodigo(), resposta));
		res.setSismamaHistoCad(questao);
		res.setServidor(rap);
		
		return res;
	}
	
	
	private Object obterResposta(String codigo, String valor) {
		Object resposta = null;
		resposta = this.respostaCheckbox(codigo, valor);
		if(resposta == null) {
			resposta = this.respostaRadio(codigo, valor);
		}
		if(resposta == null) {
			resposta = this.respostaCombo(codigo, valor);
		}
		if(resposta == null) {
			resposta = this.respostaComboString(codigo, valor);
		}
		return resposta;
	}
	
	public String converterResposta(String codigo, Object valor) {
		if (valor instanceof Boolean){
			return this.converterRespostaBoolean((Boolean)valor);			
		}else if(valor instanceof Dominio) {
			return String.valueOf(((Dominio)valor).getCodigo());
		}else if(valor instanceof DominioString) {
			return String.valueOf(((DominioString)valor).getCodigo());
		}
		return String.valueOf(valor);
	}
	
	
	private Boolean respostaCheckbox(String codigo, String valor) {
		if(DominioSismamaHistoCadCodigo.C_CLI_TCIRUR.name().equals(codigo)
			|| DominioSismamaHistoCadCodigo.C_CLI_TCIRUROM.name().equals(codigo)
			|| DominioSismamaHistoCadCodigo.C_CLI_TQUIM.name().equals(codigo)
			|| DominioSismamaHistoCadCodigo.C_CLI_TRADIO.name().equals(codigo)
			|| DominioSismamaHistoCadCodigo.C_CLI_TRADIOOM.name().equals(codigo)
			|| DominioSismamaHistoCadCodigo.C_CLI_THORM.name().equals(codigo)) {
			return this.obterRespostaBoolean(valor);
		}
		return null;
	}
	
	
	private Dominio respostaRadio(String codigo, String valor) {
		if(DominioSismamaHistoCadCodigo.C_CLI_TIPEXE.name().equals(codigo)) {
			return DominioTipoExameHistopatologico.getInstance(Integer.valueOf(valor));
		}
		else if(DominioSismamaHistoCadCodigo.C_CLI_TCANC.name().equals(codigo) 
			|| DominioSismamaHistoCadCodigo.C_CLI_GRAV.name().equals(codigo)) {
			return DominioSismamaSimNaoNaoSabe.getInstance(Integer.valueOf(valor));
		}
		else if(DominioSismamaHistoCadCodigo.C_CLI_TANT.name().equals(codigo)
			|| DominioSismamaHistoCadCodigo.C_CLI_LINFO.name().equals(codigo)) {
			return DominioSismamaSimNao.getInstance(Integer.valueOf(valor));
		}
		else if(DominioSismamaHistoCadCodigo.C_CLI_MAMA.name().equals(codigo)) {
			return DominioMamaLesao.getInstance(Integer.valueOf(valor));
		}	
		else if(DominioSismamaHistoCadCodigo.C_CLI_MAT_PROC.name().equals(codigo)) {
			return DominioSismamaProcedimentoCirurgico.getInstance(Integer.valueOf(valor));
		}
		
		return  null;
	}
	
	
	private Dominio respostaCombo(String codigo, String valor) {
		if(DominioSismamaHistoCadCodigo.C_CLI_DETEC.name().equals(codigo)) {
			return DominioDeteccaoLesao.getInstance(Integer.valueOf(valor));
		}
		else if(DominioSismamaHistoCadCodigo.C_CLI_DIAG_IM.name().equals(codigo)) {
			return DominioDiagnosticoImagem.getInstance(Integer.valueOf(valor));
		}
		else if(DominioSismamaHistoCadCodigo.C_CLI_TAM.name().equals(codigo)) {
			return DominioTamanhoLesao.getInstance(Integer.valueOf(valor));
		}
		
		return  null;
	}
	
	private DominioString respostaComboString(String codigo, String valor) {
	
		if(DominioSismamaHistoCadCodigo.C_CLI_LOCA.name().equals(codigo)) {
			return DominioLocalizacaoMamografia.getDominioPorCodigo(valor);
		}
		
		return  null;
	}
	
	private Boolean obterRespostaBoolean(String resposta) {
		if ("3".equals(resposta)) {
			return Boolean.TRUE;
		} else if ("0".equals(resposta)) {
			return Boolean.FALSE;
		}
		
		return Boolean.FALSE;
	}
	
	private String converterRespostaBoolean(boolean resposta) {
		if (resposta) {
			return "3";
		}
		return "0";
	}	

	/*public Boolean habilitarBotaoQuestaoSismamaBiopsiaHist(Map<Integer, Vector<Short>> solicitacoes) {
		Boolean habilitar = Boolean.FALSE;
		if (!solicitacoes.isEmpty() && solicitacoes.size() == 1) {
			Integer iseSoeSeq = solicitacoes.keySet().iterator().next();
			if (solicitacoes.get(iseSoeSeq).size() == 1) {
				Short iseSeqp = solicitacoes.get(iseSoeSeq).iterator().next();
				
				AelItemSolicExameHistDAO dao = getAelItemSolicExameHistDAO();
				AelItemSolicExameHist itemHist = dao.obterPorChavePrimaria(new AelItemSolicExameHistId(iseSoeSeq, iseSeqp));
				
				Short unfSeq = null;
				if (itemHist.getAelUnfExecutaExames() != null) {
					unfSeq = itemHist.getAelUnfExecutaExames().getId().getUnfSeq().getSeq();
				}
				habilitar = this.verificarQuestaoSismamaBiopsia(iseSoeSeq, iseSeqp, unfSeq, true);						
			}
		}
		return habilitar;
	}*/

	public Boolean habilitarBotaoQuestaoSismamaBiopsia(Map<Integer, Vector<Short>> solicitacoes, Boolean isHist) {
		Boolean habilitar = Boolean.FALSE;
		if (!solicitacoes.isEmpty() && solicitacoes.size() == 1) {
			Integer iseSoeSeq = solicitacoes.keySet().iterator().next();
			if (solicitacoes.get(iseSoeSeq).size() == 1) {
				Short iseSeqp = solicitacoes.get(iseSoeSeq).iterator().next();
				Short unfSeq = getUnfSeqExecutaExames(iseSoeSeq, iseSeqp, isHist);
				habilitar = this.verificarQuestaoSismamaBiopsia(iseSoeSeq, iseSeqp, unfSeq, isHist);						
			}
		}
		return habilitar;
	}
	
	private Short getUnfSeqExecutaExames(Integer iseSoeSeq, Short iseSeqp, Boolean isHist) {
		if(isHist){
			AelItemSolicExameHist item = getAelItemSolicExameHistDAO().obterPorId(iseSoeSeq, iseSeqp);
			if (item.getAelUnfExecutaExames() != null) {
				return item.getAelUnfExecutaExames().getId().getUnfSeq().getSeq();
			}
		}else{
			AelItemSolicitacaoExames item = getAelItemSolicitacaoExameDAO().obterPorId(iseSoeSeq, iseSeqp);
			if (item.getAelUnfExecutaExames() != null) {
				return item.getAelUnfExecutaExames().getId().getUnfSeq().getSeq();
			}
		}
		return null;
	}

	/**
	 * @ORADB - AELP_CHAMA_QUT_SISMAMA_VESPE (BIOPSIA)
	 */
	public Boolean verificarQuestaoSismamaBiopsia(Integer iseSoeSeq, Short iseSeqp, Short unfSeq, boolean isBuscaHistorico) {
		//verifica a necessidade de chamar a tela de quetionário da mamografia

		Boolean hasCaracteristicaUnidFuncExameSismamaHisto =
			getAghuFacade().verificarCaracteristicaUnidadeFuncional(unfSeq, ConstanteAghCaractUnidFuncionais.EXAME_SISMAMA_HISTO);
		if (hasCaracteristicaUnidFuncExameSismamaHisto) {
			Long con;
			if (isBuscaHistorico) {
				AelSismamaHistoResHistDAO dao = getAelSismamaHistoResHistDAO();
				con = dao.obterRespostasSismamaHistoCountPorSoeSeqSeqpHist(iseSoeSeq, iseSeqp);				
			} else {
				con = getAelSismamaHistoResDAO().obterRespostasSismamaHistoCountPorSoeSeqSeqp(iseSoeSeq, iseSeqp);
			}
			
			if (con > 0) {
				return Boolean.TRUE;
			}
		}
		
		return Boolean.FALSE;
	}
	
	private AelSismamaHistoResHistDAO getAelSismamaHistoResHistDAO() {
		return aelSismamaHistoResHistDAO;
	}
	
	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}
	
	private AelItemSolicExameHistDAO getAelItemSolicExameHistDAO() {
		return aelItemSolicExameHistDAO;
	}

	protected AelSismamaHistoResDAO getAelSismamaHistoResDAO() {
		return aelSismamaHistoResDAO;
	}
	
	protected AelSismamaHistoCadDAO getAelSismamaHistoCadDAO() {
		return aelSismamaHistoCadDAO;
	}
	
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
}