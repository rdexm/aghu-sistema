package br.gov.mec.aghu.controleinfeccao.vo;

import java.util.List;

import br.gov.mec.aghu.prescricaomedica.vo.ItemMedicamentoPrescricaoMedicaDetalheVO;
import br.gov.mec.aghu.core.commons.BaseBean;

public class RelatorioBuscaAtivaPacientesVO implements BaseBean {

	private static final long serialVersionUID = 3749294871137241460L;
	
	private String prontuario;
	private String nome;
	private String dataHoraIngresso;
	private String dataHoraInicio;
	private String leitoId;
	private String idade;
	
	private List<NotificacoesGeraisVO> notificacaoDoencas; //Detalhes consulta C3
	private List<NotificacoesGeraisVO> notificacaoTopografias; //Detalhes da consulta C4
	private List<NotificacoesGeraisVO> notificacaoProcedimentos; //Detalhes da consulta C5
	private List<NotificacoesGeraisVO> notificacaoFatPredisponentes; //Detalhes da consulta C6
	private List<NotificacoesGeraisVO> notificacaoNotas; //Detalhes da consulta C7
	private List<NotificacoesGeraisVO> notificacaoCirurgias; //Detalhes da consulta C8
	private List<ItemMedicamentoPrescricaoMedicaDetalheVO> detalhesMedicamentos; //Detalhes da consulta C9
	public String getProntuario() {
		return prontuario;
	}
	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getDataHoraIngresso() {
		return dataHoraIngresso;
	}
	public void setDataHoraIngresso(String dataHoraIngresso) {
		this.dataHoraIngresso = dataHoraIngresso;
	}
	public String getLeitoId() {
		return leitoId;
	}
	public void setLeitoId(String leitoId) {
		this.leitoId = leitoId;
	}
	public String getIdade() {
		return idade;
	}
	public void setIdade(String idade) {
		this.idade = idade;
	}
	public List<NotificacoesGeraisVO> getNotificacaoDoencas() {
		return notificacaoDoencas;
	}
	public void setNotificacaoDoencas(List<NotificacoesGeraisVO> notificacaoDoencas) {
		this.notificacaoDoencas = notificacaoDoencas;
	}
	public List<NotificacoesGeraisVO> getNotificacaoTopografias() {
		return notificacaoTopografias;
	}
	public void setNotificacaoTopografias(
			List<NotificacoesGeraisVO> notificacaoTopografias) {
		this.notificacaoTopografias = notificacaoTopografias;
	}
	public List<NotificacoesGeraisVO> getNotificacaoProcedimentos() {
		return notificacaoProcedimentos;
	}
	public void setNotificacaoProcedimentos(
			List<NotificacoesGeraisVO> notificacaoProcedimentos) {
		this.notificacaoProcedimentos = notificacaoProcedimentos;
	}
	public List<NotificacoesGeraisVO> getNotificacaoFatPredisponentes() {
		return notificacaoFatPredisponentes;
	}
	public void setNotificacaoFatPredisponentes(
			List<NotificacoesGeraisVO> notificacaoFatPredisponentes) {
		this.notificacaoFatPredisponentes = notificacaoFatPredisponentes;
	}
	public List<NotificacoesGeraisVO> getNotificacaoNotas() {
		return notificacaoNotas;
	}
	public void setNotificacaoNotas(List<NotificacoesGeraisVO> notificacaoNotas) {
		this.notificacaoNotas = notificacaoNotas;
	}
	public List<NotificacoesGeraisVO> getNotificacaoCirurgias() {
		return notificacaoCirurgias;
	}
	public void setNotificacaoCirurgias(
			List<NotificacoesGeraisVO> notificacaoCirurgias) {
		this.notificacaoCirurgias = notificacaoCirurgias;
	}
	public List<ItemMedicamentoPrescricaoMedicaDetalheVO> getDetalhesMedicamentos() {
		return detalhesMedicamentos;
	}
	public void setDetalhesMedicamentos(
			List<ItemMedicamentoPrescricaoMedicaDetalheVO> detalhesMedicamentos) {
		this.detalhesMedicamentos = detalhesMedicamentos;
	}
	public String getDataHoraInicio() {
		return dataHoraInicio;
	}
	public void setDataHoraInicio(String dataHoraInicio) {
		this.dataHoraInicio = dataHoraInicio;
	}
}
