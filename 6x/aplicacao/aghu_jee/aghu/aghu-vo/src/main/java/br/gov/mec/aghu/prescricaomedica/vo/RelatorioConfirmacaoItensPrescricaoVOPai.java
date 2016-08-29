package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.collections.comparators.NullComparator;

public class RelatorioConfirmacaoItensPrescricaoVOPai implements Serializable, Cloneable {

	private static final long serialVersionUID = 7836454982770205491L;

	private List<RelatorioConfirmacaoItensPrescricaoVO> dietasConfirmadas;

	private List<RelatorioConfirmacaoItensPrescricaoVO> cuidadosConfirmados;

	private List<RelatorioConfirmacaoItensPrescricaoVO> medicamentosConfirmados;

	private List<RelatorioConfirmacaoItensPrescricaoVO> solucoesConfirmadas;

	private List<RelatorioConfirmacaoItensPrescricaoVO> consultoriasConfirmadas;

	private List<RelatorioConfirmacaoItensPrescricaoVO> hemoterapiasConfirmadas;

	private List<RelatorioConfirmacaoItensPrescricaoVO> nptConfirmadas;

	private List<RelatorioConfirmacaoItensPrescricaoVO> procedimentosConfirmados;

	private String dataValidadeInicial;

	private String dataValidadeFinal;

	private String nomePaciente;

	private String nomeMaePaciente;

	private String prontuarioPaciente;

	private String prontuarioMaePaciente;

	private String localInternacao;

	private Integer sequencialPrescricao;

	private String medicoConfirmacao;

	private String grupo;

	private Integer ordemTela;
	
	private String cidPrincipalAtendimento;
	
	private String dtHoraInternacao;

	/**
	 * 
	 * @param dieta
	 */
	public void adicionarDietaConfirmada(String descricaoDieta, String operacao, Integer ordem) {
		if (dietasConfirmadas == null) {
			dietasConfirmadas = new ArrayList<RelatorioConfirmacaoItensPrescricaoVO>();
		}
		RelatorioConfirmacaoItensPrescricaoVO voDieta = new RelatorioConfirmacaoItensPrescricaoVO(
				descricaoDieta);
		voDieta.setTipo("DIETA");
		voDieta.setOperacao(operacao);
		voDieta.setOrdem(ordem);
		dietasConfirmadas.add(voDieta);
	}

	/**
	 * 
	 * @param medicamento
	 */
	public void adicionarMedicamentoConfirmado(String descricaoMedicamento,
			String aprazamento, Boolean indAntiMicrobiano, String operacao, Integer ordem) {
		if (medicamentosConfirmados == null) {
			medicamentosConfirmados = new ArrayList<RelatorioConfirmacaoItensPrescricaoVO>();
		}
		RelatorioConfirmacaoItensPrescricaoVO voMedicamento = new RelatorioConfirmacaoItensPrescricaoVO(
				descricaoMedicamento);
		voMedicamento.setTipo("MEDICAMENTOS");
		voMedicamento.setOperacao(operacao);
		voMedicamento.setOrdem(ordem);
		voMedicamento.setAprazamento(aprazamento);
		voMedicamento.setIndAntiMicrobiano(indAntiMicrobiano);
		medicamentosConfirmados.add(voMedicamento);
	}

	/**
	 * 
	 * @param cuidado
	 */
	public void adicionarCuidadoConfirmado(String descricaoCuidado,
			String aprazamento, String operacao, Integer ordem) {
		if (cuidadosConfirmados == null) {
			cuidadosConfirmados = new ArrayList<RelatorioConfirmacaoItensPrescricaoVO>();
		}
		RelatorioConfirmacaoItensPrescricaoVO voCuidado = new RelatorioConfirmacaoItensPrescricaoVO(
				descricaoCuidado);
		voCuidado.setAprazamento(aprazamento);
		voCuidado.setTipo("CUIDADOS");
		voCuidado.setOperacao(operacao);
		voCuidado.setOrdem(ordem);
		cuidadosConfirmados.add(voCuidado);
	}

	/**
	 * 
	 * @param solucao
	 */
	public void adicionarSolucaoConfirmada(String descricaoSolucao,
			String aprazamento, String operacao, Integer ordem) {
		if (solucoesConfirmadas == null) {
			solucoesConfirmadas = new ArrayList<RelatorioConfirmacaoItensPrescricaoVO>();
		}
		RelatorioConfirmacaoItensPrescricaoVO voSolucao = new RelatorioConfirmacaoItensPrescricaoVO(
				descricaoSolucao);
		voSolucao.setAprazamento(aprazamento);
		voSolucao.setTipo("SOLUÇÕES");
		voSolucao.setOperacao(operacao);
		voSolucao.setOrdem(ordem);
		solucoesConfirmadas.add(voSolucao);
	}

	/**
	 * 
	 * @param consultoria
	 */
	public void adicionarConsultoriaConfirmada(String descricaoConsultoria, String operacao, Integer ordem) {
		if (consultoriasConfirmadas == null) {
			consultoriasConfirmadas = new ArrayList<RelatorioConfirmacaoItensPrescricaoVO>();
		}
		RelatorioConfirmacaoItensPrescricaoVO voConsultoria = new RelatorioConfirmacaoItensPrescricaoVO(
				descricaoConsultoria);
		voConsultoria.setTipo("CONSULTORIA");
		voConsultoria.setOperacao(operacao);
		voConsultoria.setOrdem(ordem);
		consultoriasConfirmadas.add(voConsultoria);
	}

	/**
	 * 
	 * @param hemoterapia
	 */
	public void adicionarHemoterapiaConfirmada(String descricaoHemoterapia, String operacao, Integer ordem) {
		if (hemoterapiasConfirmadas == null) {
			hemoterapiasConfirmadas = new ArrayList<RelatorioConfirmacaoItensPrescricaoVO>();
		}
		RelatorioConfirmacaoItensPrescricaoVO voHemoterapia = new RelatorioConfirmacaoItensPrescricaoVO(
				descricaoHemoterapia);
		voHemoterapia.setTipo("HEMOTERAPIA");
		voHemoterapia.setOperacao(operacao);
		voHemoterapia.setOrdem(ordem);
		hemoterapiasConfirmadas.add(voHemoterapia);
	}

	/**
	 * 
	 * @param npt
	 */
	public void adicionarNPTConfirmada(String descricaoNPT, String operacao, Integer ordem) {
		if (nptConfirmadas == null) {
			nptConfirmadas = new ArrayList<RelatorioConfirmacaoItensPrescricaoVO>();
		}
		RelatorioConfirmacaoItensPrescricaoVO voNpt = new RelatorioConfirmacaoItensPrescricaoVO(
				descricaoNPT);
		voNpt.setTipo("NUTRIÇÃO PARENTERAL");
		voNpt.setOperacao(operacao);
		voNpt.setOrdem(ordem);
		nptConfirmadas.add(voNpt);
	}

	/**
	 * 
	 * @param procedimento
	 */
	public void adicionarProcedimentoConfirmado(String descricaoProcedimento, String operacao, Integer ordem) {
		if (procedimentosConfirmados == null) {
			procedimentosConfirmados = new ArrayList<RelatorioConfirmacaoItensPrescricaoVO>();
		}
		RelatorioConfirmacaoItensPrescricaoVO voProcedimento = new RelatorioConfirmacaoItensPrescricaoVO(
				descricaoProcedimento);
		voProcedimento.setTipo("PROCEDIMENTOS ESPECIAIS");
		voProcedimento.setOperacao(operacao);
		voProcedimento.setOrdem(ordem);
		procedimentosConfirmados.add(voProcedimento);
	}

	public void atribuirIndice() {
		Integer indice = 1;
		indice = formataIndiceDietasConfirmadas(indice);
		indice = formataIndiceCuidadosConfirmados(indice);
		indice = formataIndiceMedicamentosConfirmados(indice);
		indice = formataIndiceSolucoesConfirmadas(indice);
		indice = formataIndiceConsultoriasConfirmadas(indice);
		indice = formataIndiceHemoterapiasConfirmadas(indice);
		indice = formataIndiceNptConfirmadas(indice);
		formataIndiceProcedimentosConfirmados(indice);
	}

	private void formataIndiceProcedimentosConfirmados(Integer indice) {
		if (getProcedimentosConfirmados() != null) {
			Collections.sort(getProcedimentosConfirmados());
			for (RelatorioConfirmacaoItensPrescricaoVO vo : getProcedimentosConfirmados()) {
				vo.setNumero(indice);
				indice++;
			}
		}
	}

	private Integer formataIndiceNptConfirmadas(Integer indice) {
		if (getNptConfirmadas() != null) {
			Collections.sort(getNptConfirmadas());
			for (RelatorioConfirmacaoItensPrescricaoVO vo : getNptConfirmadas()) {
				vo.setNumero(indice);
				indice++;
			}
		}
		return indice;
	}

	private Integer formataIndiceHemoterapiasConfirmadas(Integer indice) {
		if (getHemoterapiasConfirmadas() != null) {
			Collections.sort(getHemoterapiasConfirmadas());
			for (RelatorioConfirmacaoItensPrescricaoVO vo : getHemoterapiasConfirmadas()) {
				vo.setNumero(indice);
				indice++;
			}
		}
		return indice;
	}

	private Integer formataIndiceConsultoriasConfirmadas(Integer indice) {
		if (getConsultoriasConfirmadas() != null) {
			Collections.sort(getConsultoriasConfirmadas());
			for (RelatorioConfirmacaoItensPrescricaoVO vo : getConsultoriasConfirmadas()) {
				vo.setNumero(indice);
				indice++;
			}
		}
		return indice;
	}

	private Integer formataIndiceSolucoesConfirmadas(Integer indice) {
		if (getSolucoesConfirmadas() != null) {
			Collections.sort(getSolucoesConfirmadas());
			for (RelatorioConfirmacaoItensPrescricaoVO vo : getSolucoesConfirmadas()) {
				vo.setNumero(indice);
				indice++;
			}
		}
		return indice;
	}

	private Integer formataIndiceMedicamentosConfirmados(Integer indice) {
		if (getMedicamentosConfirmados() != null) {
			Collections.sort(getMedicamentosConfirmados());
			for (RelatorioConfirmacaoItensPrescricaoVO vo : getMedicamentosConfirmados()) {
				vo.setNumero(indice);
				indice++;
			}
		}
		return indice;
	}

	private Integer formataIndiceCuidadosConfirmados(Integer indice) {
		if (getCuidadosConfirmados() != null) {
			Collections.sort(getCuidadosConfirmados());
			for (RelatorioConfirmacaoItensPrescricaoVO vo : getCuidadosConfirmados()) {
				vo.setNumero(indice);
				indice++;
			}
		}
		return indice;
	}

	private Integer formataIndiceDietasConfirmadas(Integer indice) {
		if (getDietasConfirmadas() != null) {
			Collections.sort(getDietasConfirmadas());
			for (RelatorioConfirmacaoItensPrescricaoVO vo : getDietasConfirmadas()) {
				vo.setNumero(indice);
				indice++;
			}
		}
		return indice;
	}
	
	public void ordernarListas(){
		final ComparatorChain chainSorter = new ComparatorChain();
		final BeanComparator ordemSorter = new BeanComparator("ordem", new NullComparator(false));
		chainSorter.addComparator(ordemSorter);
		if (dietasConfirmadas != null){
			Collections.sort(dietasConfirmadas, chainSorter);			
		}
		if (cuidadosConfirmados != null){
			Collections.sort(cuidadosConfirmados, chainSorter);			
		}
		if (medicamentosConfirmados != null){
			Collections.sort(medicamentosConfirmados, chainSorter);			
		}
		if (solucoesConfirmadas != null){
			Collections.sort(solucoesConfirmadas, chainSorter);			
		}
		if (consultoriasConfirmadas != null){
			Collections.sort(consultoriasConfirmadas, chainSorter);			
		}
		if (hemoterapiasConfirmadas != null){
			Collections.sort(hemoterapiasConfirmadas, chainSorter);			
		}
		if (nptConfirmadas != null){
			Collections.sort(nptConfirmadas, chainSorter);			
		}
		if (procedimentosConfirmados != null){
			Collections.sort(procedimentosConfirmados, chainSorter);			
		}
	}

	public List<RelatorioConfirmacaoItensPrescricaoVO> getDietasConfirmadas() {
		return dietasConfirmadas;
	}

	public void setDietasConfirmadas(
			List<RelatorioConfirmacaoItensPrescricaoVO> dietasConfirmadas) {
		this.dietasConfirmadas = dietasConfirmadas;
	}

	public List<RelatorioConfirmacaoItensPrescricaoVO> getCuidadosConfirmados() {
		return cuidadosConfirmados;
	}

	public void setCuidadosConfirmados(
			List<RelatorioConfirmacaoItensPrescricaoVO> cuidadosConfirmados) {
		this.cuidadosConfirmados = cuidadosConfirmados;
	}

	public List<RelatorioConfirmacaoItensPrescricaoVO> getMedicamentosConfirmados() {
		return medicamentosConfirmados;
	}

	public void setMedicamentosConfirmados(
			List<RelatorioConfirmacaoItensPrescricaoVO> medicamentosConfirmados) {
		this.medicamentosConfirmados = medicamentosConfirmados;
	}

	public List<RelatorioConfirmacaoItensPrescricaoVO> getSolucoesConfirmadas() {
		return solucoesConfirmadas;
	}

	public void setSolucoesConfirmadas(
			List<RelatorioConfirmacaoItensPrescricaoVO> solucoesConfirmadas) {
		this.solucoesConfirmadas = solucoesConfirmadas;
	}

	public List<RelatorioConfirmacaoItensPrescricaoVO> getConsultoriasConfirmadas() {
		return consultoriasConfirmadas;
	}

	public void setConsultoriasConfirmadas(
			List<RelatorioConfirmacaoItensPrescricaoVO> consultoriasConfirmadas) {
		this.consultoriasConfirmadas = consultoriasConfirmadas;
	}

	public List<RelatorioConfirmacaoItensPrescricaoVO> getHemoterapiasConfirmadas() {
		return hemoterapiasConfirmadas;
	}

	public void setHemoterapiasConfirmadas(
			List<RelatorioConfirmacaoItensPrescricaoVO> hemoterapiasConfirmadas) {
		this.hemoterapiasConfirmadas = hemoterapiasConfirmadas;
	}

	public List<RelatorioConfirmacaoItensPrescricaoVO> getNptConfirmadas() {
		return nptConfirmadas;
	}

	public void setNptConfirmadas(
			List<RelatorioConfirmacaoItensPrescricaoVO> nptConfirmadas) {
		this.nptConfirmadas = nptConfirmadas;
	}

	public List<RelatorioConfirmacaoItensPrescricaoVO> getProcedimentosConfirmados() {
		return procedimentosConfirmados;
	}

	public void setProcedimentosConfirmados(
			List<RelatorioConfirmacaoItensPrescricaoVO> procedimentosConfirmados) {
		this.procedimentosConfirmados = procedimentosConfirmados;
	}

	public String getDataValidadeInicial() {
		return dataValidadeInicial;
	}

	public void setDataValidadeInicial(String dataValidadeInicial) {
		this.dataValidadeInicial = dataValidadeInicial;
	}

	public String getDataValidadeFinal() {
		return dataValidadeFinal;
	}

	public void setDataValidadeFinal(String dataValidadeFinal) {
		this.dataValidadeFinal = dataValidadeFinal;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public String getNomeMaePaciente() {
		return nomeMaePaciente;
	}

	public void setNomeMaePaciente(String nomeMaePaciente) {
		this.nomeMaePaciente = nomeMaePaciente;
	}

	public String getProntuarioPaciente() {
		return prontuarioPaciente;
	}

	public void setProntuarioPaciente(String prontuarioPaciente) {
		this.prontuarioPaciente = prontuarioPaciente;
	}

	public String getProntuarioMaePaciente() {
		return prontuarioMaePaciente;
	}

	public void setProntuarioMaePaciente(String prontuarioMaePaciente) {
		this.prontuarioMaePaciente = prontuarioMaePaciente;
	}

	public Integer getSequencialPrescricao() {
		return sequencialPrescricao;
	}

	public void setSequencialPrescricao(Integer sequencialPrescricao) {
		this.sequencialPrescricao = sequencialPrescricao;
	}

	public String getLocalInternacao() {
		return localInternacao;
	}

	public void setLocalInternacao(String localInternacao) {
		this.localInternacao = localInternacao;
	}

	public String getMedicoConfirmacao() {
		return medicoConfirmacao;
	}

	public void setMedicoConfirmacao(String medicoConfirmacao) {
		this.medicoConfirmacao = medicoConfirmacao;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public Integer getOrdemTela() {
		return ordemTela;
	}

	public void setOrdemTela(Integer ordemTela) {
		this.ordemTela = ordemTela;
	}

	public String getCidPrincipalAtendimento() {
		return cidPrincipalAtendimento;
	}

	public void setCidPrincipalAtendimento(String cidPrincipalAtendimento) {
		this.cidPrincipalAtendimento = cidPrincipalAtendimento;
	}

	public String getDtHoraInternacao() {
		return dtHoraInternacao;
	}

	public void setDtHoraInternacao(String dtHoraInternacao) {
		this.dtHoraInternacao = dtHoraInternacao;
	}
	
	public RelatorioConfirmacaoItensPrescricaoVOPai copiar() {
		try {
			return (RelatorioConfirmacaoItensPrescricaoVOPai) this.clone();
		} catch (CloneNotSupportedException e) {
			// engolir exceção nunca vai acontecer pois o bojeto é clonnable.
			return null;
		}
	}

}