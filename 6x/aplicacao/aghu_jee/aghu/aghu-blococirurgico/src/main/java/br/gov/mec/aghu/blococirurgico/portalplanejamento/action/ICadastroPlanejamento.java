package br.gov.mec.aghu.blococirurgico.portalplanejamento.action;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.VMbcProcEsp;

public interface ICadastroPlanejamento {

	MbcAgendas getAgenda();
	AipPacientes getPaciente();
	Short getSeqUnidFuncionalCirugica();
	String gravar() throws ParseException, IllegalAccessException, InvocationTargetException;
	VMbcProcEsp getProcedimento();
}
