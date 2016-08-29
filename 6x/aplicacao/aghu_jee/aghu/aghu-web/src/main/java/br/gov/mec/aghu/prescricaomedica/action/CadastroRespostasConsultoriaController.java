package br.gov.mec.aghu.prescricaomedica.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioFinalizacao;
import br.gov.mec.aghu.model.MpmRespostaConsultoria;
import br.gov.mec.aghu.model.MpmRespostaConsultoriaId;
import br.gov.mec.aghu.model.MpmSolicitacaoConsultoria;
import br.gov.mec.aghu.model.MpmTipoRespostaConsultoria;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;

public class CadastroRespostasConsultoriaController extends ActionController {

	private static final long serialVersionUID = -2869134257213228902L;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	// Parâmetros de conversação
	private Integer scnAtdSeq;
	private Integer scnSeq;
	private String voltarPara;
	
	// Respostas que serão cadastradas
	private List<MpmRespostaConsultoria> listaRespostas = new ArrayList<MpmRespostaConsultoria>();
	
	private String prontuarioFormatado;
	private MpmSolicitacaoConsultoria solicitacaoConsultoria;
	private List<MpmTipoRespostaConsultoria> tiposResposta;
	
	private DominioFinalizacao indFinalizacao;
	
	private static final String CONSULTA_RESPOSTAS_CONSULTORIA = "prescricaomedica-consultaRespostasConsultoria";
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void inicio() {
		if (listaRespostas == null || listaRespostas.isEmpty()) {
			listaRespostas = new ArrayList<MpmRespostaConsultoria>();
			solicitacaoConsultoria = this.prescricaoMedicaFacade.obterMpmSolicitacaoConsultoriaPorIdComPaciente(scnAtdSeq, scnSeq);
			prontuarioFormatado = CoreUtil.formataProntuario(this.solicitacaoConsultoria.getPrescricaoMedica().getAtendimento().getPaciente().getProntuario());
			tiposResposta = this.prescricaoMedicaFacade.pesquisarTiposRespostasConsultoria(solicitacaoConsultoria.getIndConcluida());
			adicionarResposta();		
		}
	}
	
	public void adicionarResposta() {
		
		if (listaRespostas.size() <= (tiposResposta.size() - 1)) {
			MpmRespostaConsultoriaId id = new MpmRespostaConsultoriaId();
			id.setScnAtdSeq(scnAtdSeq);
			id.setScnSeq(scnSeq);
			id.setTrcSeq(null);
			id.setCriadoEm(new Date());
			MpmRespostaConsultoria respostaConsultoria = new MpmRespostaConsultoria();
			respostaConsultoria.setId(id);
			respostaConsultoria.setSolicitacaoConsultoria(solicitacaoConsultoria);
			listaRespostas.add(respostaConsultoria);
		}
	}
	
	public void removerResposta(Object item) {
		if (item != null) {
			listaRespostas.remove(item);
		}
	}
	
	public String gravar() {	
		try {
			for (MpmRespostaConsultoria mpmRespostaConsultoria : listaRespostas) {
				mpmRespostaConsultoria.setFinalizacao(indFinalizacao);
				mpmRespostaConsultoria.getId().setTrcSeq(mpmRespostaConsultoria.getTipoRespostaConsultoria().getSeq());
			}
			this.prescricaoMedicaFacade.inserirRespostasConsultoria(listaRespostas, indFinalizacao);
			this.apresentarMsgNegocio(Severity.INFO, "MSG_RESPONDER_CONSULTORIA_SUCESSO");
			return voltar();
		} catch (BaseListException e) {
			apresentarExcecaoNegocio(e);
			return null;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}
	
	public String voltar() {		
		String voltar = voltarPara;
		this.limpar();
		return voltar;
	}
	
	public void limpar() {
		scnAtdSeq = null;
		scnSeq = null;
		listaRespostas = null;
		solicitacaoConsultoria = null;
		tiposResposta = null;
		indFinalizacao = null;
		voltarPara = null;
	}
	
	public String verRespostas() {
		return CONSULTA_RESPOSTAS_CONSULTORIA;
	}
	
	// Getters e Setters

	public DominioFinalizacao getIndFinalizacao(String valor) {
		return DominioFinalizacao.getInstance(valor);
	}
	
	public String getIndFinalizacaoDescricao(String valor) {
		return DominioFinalizacao.getInstance(valor).getDescricao();
	}
	
	public Integer getScnAtdSeq() {
		return scnAtdSeq;
	}

	public void setScnAtdSeq(Integer scnAtdSeq) {
		this.scnAtdSeq = scnAtdSeq;
	}

	public Integer getScnSeq() {
		return scnSeq;
	}

	public void setScnSeq(Integer scnSeq) {
		this.scnSeq = scnSeq;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public MpmSolicitacaoConsultoria getSolicitacaoConsultoria() {
		return solicitacaoConsultoria;
	}

	public void setSolicitacaoConsultoria(
			MpmSolicitacaoConsultoria solicitacaoConsultoria) {
		this.solicitacaoConsultoria = solicitacaoConsultoria;
	}

	public List<MpmTipoRespostaConsultoria> getTiposResposta() {
		return tiposResposta;
	}

	public void setTiposResposta(List<MpmTipoRespostaConsultoria> tiposResposta) {
		this.tiposResposta = tiposResposta;
	}

	public DominioFinalizacao getIndFinalizacao() {
		return indFinalizacao;
	}

	public void setIndFinalizacao(DominioFinalizacao indFinalizacao) {
		this.indFinalizacao = indFinalizacao;
	}

	public List<MpmRespostaConsultoria> getListaRespostas() {
		return listaRespostas;
	}

	public void setListaRespostas(List<MpmRespostaConsultoria> listaRespostas) {
		this.listaRespostas = listaRespostas;
	}

	public String getProntuarioFormatado() {
		return prontuarioFormatado;
	}

	public void setProntuarioFormatado(String prontuarioFormatado) {
		this.prontuarioFormatado = prontuarioFormatado;
	}
}
