package br.gov.mec.aghu.prescricaomedica.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.transaction.SystemException;

import org.apache.commons.lang3.text.WordUtils;
import org.jfree.util.Log;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.model.AghAtendimentoPacientes;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.prescricaomedica.vo.RelSumarioPrescricaoVO;
import net.sf.jasperreports.engine.JRException;

/**
 * @author cqsilva
 */

public class RelatorioPrescricaoMedicaCallBack extends ActionReport {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}



	private static final long serialVersionUID = -4499370151697274806L;
	
	private List<RelSumarioPrescricaoVO> colecao = new ArrayList<RelSumarioPrescricaoVO>(0);
	
	private AghAtendimentoPacientes atendimentoPaciente;
	
	private AghAtendimentos atendimento;
	
	List<String> listaDias = new ArrayList<String>();
	
	private String nomePaciente;
	
	private String prontuario;
	
	private String nomeEquipe;
	
	private String localizacao;
	
	private Date dataInternacao;
	
	private Date dataAlta;
	
	private Date dataInicioPeriodo;
	
	private Date dataFimPeriodo;
	
	private Date dataAtual;
	
	private String caminhoLogo;
	
	private String prontuarioMae;


	public RelatorioPrescricaoMedicaCallBack() {
		super();
	}	
	
	public RelatorioPrescricaoMedicaCallBack(
			List<RelSumarioPrescricaoVO> colecao, AghAtendimentos atendimento,
			AghAtendimentoPacientes atendimentoPaciente,
			List<String> listaDias, String caminhoLogo, Date dataInicioPeriodo,
			Date dataFimPeriodo, String nomeEquipe, Date dataInternacao, String prontuarioMae) {
		super();
		this.atendimento = atendimento;
		this.atendimentoPaciente = atendimentoPaciente;
		this.colecao = colecao;
		this.listaDias = listaDias;
		this.caminhoLogo = caminhoLogo;
		this.dataInicioPeriodo = dataInicioPeriodo;
		this.dataFimPeriodo = dataFimPeriodo;
		this.nomeEquipe = nomeEquipe;
		this.dataInternacao = dataInternacao;
		this.prontuarioMae = prontuarioMae;
	}
	
	public byte[] geraPdf(boolean protegido) throws BaseException, JRException,
			SystemException, IOException, DocumentException {

		DocumentoJasper documento = null;
		byte[] arquivoGerado = null;

		if (getColecao() != null && !getColecao().isEmpty()) {
			documento = gerarDocumento();
			arquivoGerado = documento.getPdfByteArray(protegido);
		}

		return arquivoGerado;
	}
	

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/prescricaomedica/report/relatorioSumarioPrescricao.jasper";
	}

	
	@Override
	public List<RelSumarioPrescricaoVO> recuperarColecao() throws ApplicationBusinessException {
		return this.colecao;
	}

	@Override
	@SuppressWarnings("PMD.NPathComplexity")
	public Map<String, Object> recuperarParametros() {
		
		if (this.atendimento != null && this.atendimento.getPaciente()!=null){
			this.nomePaciente = WordUtils.capitalizeFully(this.atendimento.getPaciente().getNome());
		}

		if (this.atendimento != null && this.atendimento.getPaciente()!=null){
			this.prontuario = CoreUtil.formataProntuarioRelatorio(this.atendimento.getPaciente().getProntuario());
		}

		if (this.atendimento != null
				&& this.atendimento.getEspecialidade() != null
				&& this.nomeEquipe != null) {
			this.nomeEquipe = this.nomeEquipe + " - " + WordUtils.capitalizeFully(this.atendimento.getEspecialidade().getNomeEspecialidade());
		}

		if (this.atendimento != null){
			if (this.atendimento.getLeito()!=null){
				this.localizacao = "Leito: " + this.atendimento.getLeito().getLeitoID();	
			}else if (this.atendimento.getQuarto()!=null){
				this.localizacao = "Quarto: " + this.atendimento.getQuarto().getDescricao();
			}else if (this.atendimento.getUnidadeFuncional() != null && this.atendimento.getUnidadeFuncional().getSigla()!=null){
				this.localizacao = "Unidade: " + this.atendimento.getUnidadeFuncional().getSigla();
			}else if (this.atendimento.getUnidadeFuncional() != null && this.atendimento.getUnidadeFuncional().getAndarAlaDescricao() != null){
				this.localizacao = "Unidade: " + this.atendimento.getUnidadeFuncional().getAndarAlaDescricao();
			}
		}		

		if (this.atendimento != null
				&& this.atendimento.getInternacao() != null
				&& this.atendimento.getInternacao().getDthrAltaMedica() != null) {
			this.dataAlta = this.atendimento.getInternacao()
					.getDthrAltaMedica();
		}

		this.dataAtual = new Date();
		
		if (caminhoLogo == null){
			try {
				this.caminhoLogo = this.recuperarCaminhoLogo();
			} catch (ApplicationBusinessException e) {
				Log.error(e.getMessage(),e);
			}
		}
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("nomeRelatorio", "MPMR_SUMARIO_PRCR");
		params.put("nomePaciente", this.nomePaciente);
		params.put("prontuario", this.prontuario);
		params.put("nomeEquipe", this.nomeEquipe);
		params.put("dataInternacao", this.dataInternacao);
		params.put("dataAlta", this.dataAlta);
		params.put("dataInicioPeriodo", this.dataInicioPeriodo);
		params.put("dataFimPeriodo", this.dataFimPeriodo);
		params.put("dataAtual", this.dataAtual);
		params.put("localizacao", this.localizacao);
		params.put("caminhoLogo", this.caminhoLogo);
		params.put("listaDias", this.listaDias);
		params.put("prontuarioMae", this.prontuarioMae);

		return params;
	}
	
	public List<RelSumarioPrescricaoVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<RelSumarioPrescricaoVO> colecao) {
		this.colecao = colecao;
	}

	public AghAtendimentoPacientes getAtendimentoPaciente() {
		return atendimentoPaciente;
	}

	public void setAtendimentoPaciente(AghAtendimentoPacientes atendimentoPaciente) {
		this.atendimentoPaciente = atendimentoPaciente;
	}

	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}

	public List<String> getListaDias() {
		return listaDias;
	}

	public void setListaDias(List<String> listaDias) {
		this.listaDias = listaDias;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public String getProntuario() {
		return prontuario;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}

	public String getNomeEquipe() {
		return nomeEquipe;
	}

	public void setNomeEquipe(String nomeEquipe) {
		this.nomeEquipe = nomeEquipe;
	}

	public String getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}

	public Date getDataInternacao() {
		return dataInternacao;
	}

	public void setDataInternacao(Date dataInternacao) {
		this.dataInternacao = dataInternacao;
	}

	public Date getDataAlta() {
		return dataAlta;
	}

	public void setDataAlta(Date dataAlta) {
		this.dataAlta = dataAlta;
	}

	public Date getDataInicioPeriodo() {
		return dataInicioPeriodo;
	}

	public void setDataInicioPeriodo(Date dataInicioPeriodo) {
		this.dataInicioPeriodo = dataInicioPeriodo;
	}

	public Date getDataFimPeriodo() {
		return dataFimPeriodo;
	}

	public void setDataFimPeriodo(Date dataFimPeriodo) {
		this.dataFimPeriodo = dataFimPeriodo;
	}

	public Date getDataAtual() {
		return dataAtual;
	}

	public void setDataAtual(Date dataAtual) {
		this.dataAtual = dataAtual;
	}

	public void setCaminhoLogo(String caminhoLogo) {
		this.caminhoLogo = caminhoLogo;
	}

	public String getCaminhoLogo() {
		return caminhoLogo;
	}

	public String getProntuarioMae() {
		return prontuarioMae;
	}

	public void setProntuarioMae(String prontuarioMae) {
		this.prontuarioMae = prontuarioMae;
	}

}