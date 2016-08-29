package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.MpmSolicitacaoConsultoriaVO;
import br.gov.mec.aghu.core.action.ActionController;

public class ConsultaDetalheConsultoriaController extends ActionController {

	private static final long serialVersionUID = 6042571137962583729L;

	private Integer atdSeq;	
	private String motivo;	
	private String resposta;	
	private boolean exibirCancelarDetalheInternacao = false;	
	private String voltarPara;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	private List<MpmSolicitacaoConsultoriaVO> solicitacoes = new ArrayList<MpmSolicitacaoConsultoriaVO>(0);
	
	private MpmSolicitacaoConsultoriaVO selecionado;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}

	public void inicio() {
		solicitacoes = prescricaoMedicaFacade.pesquisaSolicitacoesConsultoriaVO(atdSeq);
		resposta = null;
		if(solicitacoes != null && !solicitacoes.isEmpty()) {
			selecionado = solicitacoes.get(0);
			this.setMotivo((StringUtils.isNotBlank(solicitacoes.get(0).getMotivo()))?solicitacoes.get(0).getMotivo().replaceAll("\n", "<BR/>"):null);
			this.setResposta(processarResposta(1));
		}
	}

	public boolean possuiConsultoriasAtivas(Integer atdSeq) {		
		Long sol = prescricaoMedicaFacade.pesquisaSolicitacoesConsultoriaCount(atdSeq);
		
		if(sol != null && sol.intValue() > 0) {
			return true;
		}
		return false;
	}
	
	public void carregaMotivoResposta() {
		this.setMotivo((StringUtils.isNotBlank(selecionado.getMotivo()))? selecionado.getMotivo().replaceAll("\n", "<BR/>"):null);
		this.setResposta(processarResposta(1));
	}
	
	public String processarResposta(Integer ordem){
		String resposta = prescricaoMedicaFacade.obterRespostasConsultoria(selecionado.getAtdSeq(), selecionado.getSeq(), ordem);
		if(StringUtils.isNotBlank(resposta)){
			resposta = resposta.replaceAll("\n", "<BR/>");
			return resposta;
		}else{
			return null;
		}
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public List<MpmSolicitacaoConsultoriaVO> getSolicitacoes() {
		return solicitacoes;
	}

	public void setSolicitacoes(List<MpmSolicitacaoConsultoriaVO> solicitacoes) {
		this.solicitacoes = solicitacoes;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public String getResposta() {
		return resposta;
	}

	public void setResposta(String resposta) {
		this.resposta = resposta;
	}

	public boolean isExibirCancelarDetalheInternacao() {
		return exibirCancelarDetalheInternacao;
	}

	public void setExibirCancelarDetalheInternacao(
			boolean exibirCancelarDetalheInternacao) {
		this.exibirCancelarDetalheInternacao = exibirCancelarDetalheInternacao;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public MpmSolicitacaoConsultoriaVO getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(MpmSolicitacaoConsultoriaVO selecionado) {
		this.selecionado = selecionado;
	}
}