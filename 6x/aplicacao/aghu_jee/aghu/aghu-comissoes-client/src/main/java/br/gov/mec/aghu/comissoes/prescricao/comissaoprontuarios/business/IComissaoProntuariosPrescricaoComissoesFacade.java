package br.gov.mec.aghu.comissoes.prescricao.comissaoprontuarios.business;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.model.AipLogProntOnlines;
import br.gov.mec.aghu.model.MpmMotivoReinternacao;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public interface IComissaoProntuariosPrescricaoComissoesFacade extends
		Serializable {

	public MpmMotivoReinternacao inserirAtualizarMotivoReinternacao(
			MpmMotivoReinternacao elemento)
			throws ApplicationBusinessException;

	public MpmMotivoReinternacao obterMotivoReinternacaoPorChavePrimaria(
			Integer chavePrimaria);

	public List<MpmMotivoReinternacao> pesquisarMotivoReinternacao(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, MpmMotivoReinternacao elemento);

	public Long pesquisarMotivoReinternacaoCount(
			MpmMotivoReinternacao elemento);

	public void removerMotivoReinternacao(final Integer seq) throws ApplicationBusinessException;

	public String gerarCSVLogProntuarioOnline(List<AipLogProntOnlines> logs) throws IOException;

	void validarIntervaloData(Date inicio, Date fim) throws ApplicationBusinessException;

	String nameHeaderEfetuarDownloadCSVLogProntuarioOnline(String fileName);

}