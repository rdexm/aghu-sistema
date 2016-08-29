package br.gov.mec.aghu.business.scheduler;

import br.gov.mec.aghu.core.dominio.Dominio;
import br.gov.mec.aghu.model.RapServidores;

public interface IAutomaticJobEnum extends Dominio {

	int getCodigo();

	String getDescricao();

	String toString();

	String getTriggerName();

	String getCron();

	void setCron(String c);

	void setServidor(RapServidores servidor);

	RapServidores getServidor();

	boolean isPermiteVariosAgendamentos();

	void setIgnorarLimiteMinIntervaloAgendamento(
			boolean ignorarLimiteMinIntervaloAgendamento);

	boolean isIgnorarLimiteMinIntervaloAgendamento();

}