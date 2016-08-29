package br.gov.mec.aghu.ambulatorio.vo;

import java.util.List;

import br.gov.mec.aghu.model.AelRespostaQuestao;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.MamAltaSumario;
import br.gov.mec.aghu.model.MamAnamneses;
import br.gov.mec.aghu.model.MamAtestados;
import br.gov.mec.aghu.model.MamEvolucoes;
import br.gov.mec.aghu.model.MamInterconsultas;
import br.gov.mec.aghu.model.MamLaudoAih;
import br.gov.mec.aghu.model.MamReceituarios;
import br.gov.mec.aghu.model.MamSolicProcedimento;

public class DocumentosPacienteVO {
	private Boolean selecionado;
	private String descricao;
	private Boolean imprimiu;
	private AelSolicitacaoExames solicitacaoExame;
	private MamReceituarios receituarios;
	private MamAnamneses anamnese;
	private MamEvolucoes evolucao;
	private MamAtestados atesdado;
	private AelRespostaQuestao respostaQuestao;
	private AtestadoVO atestado;
	private CursorCurTicketVO documentoTicketRetorno;
	private MamRelatorioVO mamRelatorioVO;
	private MamAltaSumario altaSumario;
	private MamLaudoAih laudoAih;
	private List<MamReceituarioCuidadoVO> receituarioCuidado;
	private MamInterconsultas interconsultas;
	private List<RelatorioConsultoriaAmbulatorialVO> consultoriaAmbulatorial;	
	private MamSolicProcedimento MamSolicProcedimento;
	private RelatorioTratamentoFisiatricoVO relatorioTratamentoFisiatricoVO;
	private AelSolicitacaoExames laudoAIHSolicVO;
	private String materialSolicitado;
	private Integer pacCodigo;
	private Integer numeroConsulta;
	private Boolean guiaAtendimentoUnimed = false;
	
	public MamInterconsultas getInterconsultas() {
		return interconsultas;
	}

	public List<RelatorioConsultoriaAmbulatorialVO> getConsultoriaAmbulatorial() {
		return consultoriaAmbulatorial;
	}

	public void setInterconsultas(MamInterconsultas interconsultas) {
		this.interconsultas = interconsultas;
	}
	public void setConsultoriaAmbulatorial(List<RelatorioConsultoriaAmbulatorialVO> consultoriaAmbulatorial) {
		this.consultoriaAmbulatorial = consultoriaAmbulatorial;
	}
	public Boolean getSelecionado() {
		return selecionado;
	}
	
	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setImprimiu(Boolean imprimiu) {
		this.imprimiu = imprimiu;
	}

	public Boolean getImprimiu() {
		return imprimiu;
	}

	public void setReceituarios(MamReceituarios receituarios) {
		this.receituarios = receituarios;
	}

	public MamReceituarios getReceituarios() {
		return receituarios;
	}

	public void setAnamnese(MamAnamneses anamnese) {
		this.anamnese = anamnese;
	}

	public MamAnamneses getAnamnese() {
		return anamnese;
	}

	public void setEvolucao(MamEvolucoes evolucao) {
		this.evolucao = evolucao;
	}

	public MamEvolucoes getEvolucao() {
		return evolucao;
	}

	public MamAtestados getAtesdado() {
		return atesdado;
	}

	public void setAtesdado(MamAtestados atesdado) {
		this.atesdado = atesdado;
	}

	public void setSolicitacaoExame(AelSolicitacaoExames solicitacaoExame) {
		this.solicitacaoExame = solicitacaoExame;
	}

	public AelSolicitacaoExames getSolicitacaoExame() {
		return solicitacaoExame;
	}

	public AelRespostaQuestao getRespostaQuestao() {
		return respostaQuestao;
	}

	public void setRespostaQuestao(AelRespostaQuestao respostaQuestao) {
		this.respostaQuestao = respostaQuestao;
	}
	
	public List<MamReceituarioCuidadoVO> getReceituarioCuidado() {
		return receituarioCuidado;
	}

	public void setReceituarioCuidado(List<MamReceituarioCuidadoVO> receituarioCuidado) {
		this.receituarioCuidado = receituarioCuidado;
	}

	public AtestadoVO getAtestado() {
		return atestado;
	}

	public void setAtestado(AtestadoVO atestado) {
		this.atestado = atestado;
	}

	public CursorCurTicketVO getDocumentoTicketRetorno() {
		return documentoTicketRetorno;
	}
	
	public void setDocumentoTicketRetorno(CursorCurTicketVO documentoTicketRetorno) {
		this.documentoTicketRetorno = documentoTicketRetorno;
	}

	public MamRelatorioVO getMamRelatorioVO() {
		return mamRelatorioVO;
	}

	public void setMamRelatorioVO(MamRelatorioVO mamRelatorioVO) {
		this.mamRelatorioVO = mamRelatorioVO;
	}

	public MamAltaSumario getAltaSumario() {
		return altaSumario;
	}

	public void setAltaSumario(MamAltaSumario altaSumario) {
		this.altaSumario = altaSumario;
	}

	public MamLaudoAih getLaudoAih() {
		return laudoAih;
	}

	public void setLaudoAih(MamLaudoAih laudoAih) {
		this.laudoAih = laudoAih;
	}
	public MamSolicProcedimento getMamSolicProcedimento() {
		return MamSolicProcedimento;
	}

	public void setMamSolicProcedimento(MamSolicProcedimento mamSolicProcedimento) {
		MamSolicProcedimento = mamSolicProcedimento;
	}

	public RelatorioTratamentoFisiatricoVO getRelatorioTratamentoFisiatricoVO() {
		return relatorioTratamentoFisiatricoVO;
	}

	public void setRelatorioTratamentoFisiatricoVO(
			RelatorioTratamentoFisiatricoVO relatorioTratamentoFisiatricoVO) {
		this.relatorioTratamentoFisiatricoVO = relatorioTratamentoFisiatricoVO;
	}
		
	public AelSolicitacaoExames getLaudoAIHSolicVO() {
		return laudoAIHSolicVO;
	}

	public void setLaudoAIHSolicVO(AelSolicitacaoExames laudoAIHSolicVO) {
		this.laudoAIHSolicVO = laudoAIHSolicVO;
	}

	public String getMaterialSolicitado() {
		return materialSolicitado;
	}

	public void setMaterialSolicitado(String materialSolicitado) {
		this.materialSolicitado = materialSolicitado;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
		
	public Integer getNumeroConsulta() {
		return numeroConsulta;
	}

	public void setNumeroConsulta(Integer numeroConsulta) {
		this.numeroConsulta = numeroConsulta;
	}
	
	public Boolean getGuiaAtendimentoUnimed() {
		return guiaAtendimentoUnimed;
	}
	
	public void setGuiaAtendimentoUnimed(Boolean guiaAtendimentoUnimed) {
		this.guiaAtendimentoUnimed = guiaAtendimentoUnimed;
	}
		
}