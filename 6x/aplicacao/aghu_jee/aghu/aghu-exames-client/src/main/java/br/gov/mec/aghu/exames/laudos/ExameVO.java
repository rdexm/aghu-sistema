package br.gov.mec.aghu.exames.laudos;

import java.io.Serializable;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author cvagheti
 * 
 */
public class ExameVO implements Serializable, Comparable<ExameVO> {

	private static final long serialVersionUID = -6877396418239231221L;
	
	public static final String  V_FONTS_ALTERADAS = "fontsAlteradasGeracaoLaudo";

	private Integer solicitacao;
	private Short item;
	private String convenio;
	private String origem;

	private String sigla;
	private String descricao;
	private String materialAnalise;
	private String regiaoAnatomica;
	private boolean mostraDescricao;
	
	private String vlrRef;

	private Date recebimento;
	private Date liberacao;

	private String nomeMedicoSolicitante;

	private Short idUnidade;
	private String servico;
	private String chefeServico;
	private String conselhoChefeServico;
	private String nroConselhoChefeServico;
	private String registroConselhoServico;
	private EnderecoContatosVO enderecoContatos;
	private String unidade;
	private String chefeUnidade;
	private String conselhoUnidade;
	private String nroConselhoUnidade;

	private String assinaturaMedico;
	private String assinaturaEletronica;
	
	private List<MascaraVO> mascaras;

	private List<NotaAdicionalVO> notas;

	private List<String> informacoesColeta;

	private String informacoesRespiracao;
	
	private String nomeExame;
	
	private List<String> listaRecebLiberacao;
	
	private String nomePaciente;
	
	private Integer prontuario;
	
	private boolean isAtdDiverso = false;

	public Integer getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(Integer solicitacao) {
		this.solicitacao = solicitacao;
	}

	public Short getItem() {
		return item;
	}

	public void setItem(Short item) {
		this.item = item;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getMaterialAnalise() {
		return materialAnalise;
	}

	public void setMaterialAnalise(String materialAnalise) {
		this.materialAnalise = materialAnalise;
	}

	public Date getRecebimento() {
		return recebimento;
	}

	public void setRecebimento(Date recebimento) {
		this.recebimento = recebimento;
	}

	public Date getLiberacao() {
		return liberacao;
	}

	public void setLiberacao(Date liberacao) {
		this.liberacao = liberacao;
	}

	public String getNomeMedicoSolicitante() {
		return nomeMedicoSolicitante;
	}

	public void setNomeMedicoSolicitante(String nomeMedicoSolicitante) {
		this.nomeMedicoSolicitante = nomeMedicoSolicitante;
	}

	/**
	 * Resultado do exame inclusive layout.
	 * 
	 * @return
	 */
	public List<MascaraVO> getMascaras() {
		return mascaras;
	}

	public void setMascaras(List<MascaraVO> mascaras) {
		this.mascaras = mascaras;
	}


	public String getRegiaoAnatomica() {
		return regiaoAnatomica;
	}

	public void setRegiaoAnatomica(String regiaoAnatomica) {
		this.regiaoAnatomica = regiaoAnatomica;
	}

	public List<NotaAdicionalVO> getNotas() {
		return notas;
	}

	public void setNotas(List<NotaAdicionalVO> notas) {
		this.notas = notas;
	}

	public boolean isMostraDescricao() {
		return mostraDescricao;
	}

	public void setMostraDescricao(boolean mostraDescricao) {
		this.mostraDescricao = mostraDescricao;
	}

	public String getConvenio() {
		return convenio;
	}

	public void setConvenio(String convenio) {
		this.convenio = convenio;
	}

	public Short getIdUnidade() {
		return idUnidade;
	}

	public void setIdUnidade(Short idUnidade) {
		this.idUnidade = idUnidade;
	}

	public String getServico() {
		return servico;
	}

	public void setServico(String servico) {
		this.servico = servico;
	}

	public String getChefeServico() {
		return chefeServico;
	}

	public void setChefeServico(String chefeServico) {
		this.chefeServico = chefeServico;
	}

	public String getConselhoChefeServico() {
		return conselhoChefeServico;
	}

	public void setConselhoChefeServico(String conselhoChefeServico) {
		this.conselhoChefeServico = conselhoChefeServico;
	}

	public String getNroConselhoChefeServico() {
		return nroConselhoChefeServico;
	}

	public void setNroConselhoChefeServico(String nroConselhoChefeServico) {
		this.nroConselhoChefeServico = nroConselhoChefeServico;
	}

	public String getRegistroConselhoServico() {
		return registroConselhoServico;
	}

	public void setRegistroConselhoServico(String registroConselhoServico) {
		this.registroConselhoServico = registroConselhoServico;
	}

	public EnderecoContatosVO getEnderecoContatos() {
		return enderecoContatos;
	}

	public void setEnderecoContatos(EnderecoContatosVO enderecoContatos) {
		this.enderecoContatos = enderecoContatos;
	}

	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

	public String getChefeUnidade() {
		return chefeUnidade;
	}

	public void setChefeUnidade(String chefeUnidade) {
		this.chefeUnidade = chefeUnidade;
	}

	public String getConselhoUnidade() {
		return conselhoUnidade;
	}

	public void setConselhoUnidade(String conselhoUnidade) {
		this.conselhoUnidade = conselhoUnidade;
	}

	public String getNroConselhoUnidade() {
		return nroConselhoUnidade;
	}

	public void setNroConselhoUnidade(String nroConselhoUnidade) {
		this.nroConselhoUnidade = nroConselhoUnidade;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public String getInformacoesRespiracao() {
		return informacoesRespiracao;
	}

	public void setInformacoesRespiracao(String informacoesRespiracao) {
		this.informacoesRespiracao = informacoesRespiracao;
	}

	public List<String> getInformacoesColeta() {
		return informacoesColeta;
	}

	public void setInformacoesColeta(List<String> informacoesColeta) {
		this.informacoesColeta = informacoesColeta;
	}

	public boolean hasMaterialAnalise() {
		return StringUtils.isNotBlank(this.materialAnalise);
	}

	/**
	 * Retorna a descrição do exame formatado com nome do exame com material de
	 * analise e/ou região anatômica. Exemplo: DESCRICAO EXAME(MATERIAL), <br />
	 * DESCRICAO EXAME(REGIAO), <br />
	 * DESCRICAO EXAME(MATERIAL - REGIAO)
	 */
	public String formataDescricaoExame() {
		StringBuilder descricao = new StringBuilder(this.getDescricao());
		if (this.getMaterialAnalise() != null
				&& this.getRegiaoAnatomica() != null) {
			descricao.append('(').append(this.getMaterialAnalise())
					.append(" - ").append(this.getRegiaoAnatomica())
					.append(')');
		} else if (this.getMaterialAnalise() != null) {
			descricao.append('(').append(this.getMaterialAnalise()).append(')');
		} else if (this.getRegiaoAnatomica() != null) {
			descricao.append('(').append(this.getRegiaoAnatomica()).append(')');
		}

		return descricao.toString();
	}

	public String getRecebimentoLiberacao() {
		StringBuffer result = new StringBuffer(50);

		if (this.hasMaterialAnalise()) {
			result.append("Recebimento material: ");
		} else {
			result.append("Recebimento paciente: ");
		}

		if (this.getRecebimento() != null) {
			result.append(new SimpleDateFormat("dd/MM/yy HH:mm", new Locale("pt",
					"BR")).format(this.getRecebimento()));
		}

		if (this.getLiberacao() != null) {
			result.append( " Liberado em: "
					+ new SimpleDateFormat("dd/MM/yy HH:mm", new Locale("pt",
							"BR")).format(this.getLiberacao()));
		}

		return result.toString();
	}

	
	public String getAssinaturaMedico() {
		return assinaturaMedico;
	}

	
	public void setAssinaturaMedico(String assinaturaMedico) {
		this.assinaturaMedico = assinaturaMedico;
	}

	
	public String getAssinaturaEletronica() {
		return assinaturaEletronica;
	}

	
	public void setAssinaturaEletronica(String assinaturaEletronica) {
		this.assinaturaEletronica = assinaturaEletronica;
	}

	@Override
	public int compareTo(ExameVO other) {
		
		Collator collator = Collator.getInstance(new Locale("pt", "BR"));
		
		if (unidade != null && other != null && !unidade.equalsIgnoreCase(other.getUnidade())) {
            return collator.compare(unidade, other.getUnidade());
		}
		
		if (descricao!= null && other != null && !descricao.equalsIgnoreCase(other.getDescricao())) {
            return collator.compare(descricao, other.getDescricao());
		}
		return 0;
	}

	public String getNomeExame() {
		return nomeExame;
	}

	public void setNomeExame(String nomeExame) {
		this.nomeExame = nomeExame;
	}

	public List<String> getListaRecebLiberacao() {
		return listaRecebLiberacao;
	}

	public void setListaRecebLiberacao(List<String> listaRecebLiberacao) {
		this.listaRecebLiberacao = listaRecebLiberacao;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	
	public boolean isAtdDiverso() {
		return isAtdDiverso;
	}

	
	public void setAtdDiverso(boolean isAtdDiverso) {
		this.isAtdDiverso = isAtdDiverso;
	}
	
	public void setVlrRef(String vlrRef) {
		this.vlrRef = vlrRef;		
	}

	public String getVlrRef() {
		return vlrRef;
	}
	
}
