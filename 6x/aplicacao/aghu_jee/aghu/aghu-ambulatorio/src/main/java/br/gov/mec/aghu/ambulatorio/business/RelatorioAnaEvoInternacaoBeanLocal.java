package br.gov.mec.aghu.ambulatorio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.CseCategoriaProfissional;
import br.gov.mec.aghu.paciente.vo.RelatorioAnaEvoInternacaoVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Local
public interface RelatorioAnaEvoInternacaoBeanLocal {

	public abstract List<RelatorioAnaEvoInternacaoVO> pesquisarRelatorioAnaEvoInternacao(
			Integer atdSeq, String tipoRelatorio,
			Date dataInicial, Date dataFinal)
			throws ApplicationBusinessException;

	public abstract List<RelatorioAnaEvoInternacaoVO> complementarDadosComTipoItemAnamnese(
			List<RelatorioAnaEvoInternacaoVO> list);

	public abstract List<RelatorioAnaEvoInternacaoVO> complementarDadosComTipoItemEvolucao(
			List<RelatorioAnaEvoInternacaoVO> list) throws ApplicationBusinessException;

	public abstract void complementarDadosRelatorioAnamneses(
			RelatorioAnaEvoInternacaoVO vo,
			AghParametros parametroMed, AghParametros parametroEnf,
			AghParametros parametroNut)
			throws ApplicationBusinessException;

	public abstract void complementarDadosRelatorioEvolucao(
			RelatorioAnaEvoInternacaoVO vo,
			CseCategoriaProfissional categoriaProfissional)
			throws ApplicationBusinessException;

	public abstract void complementarRodapeRelatorioAnaEvoInternacao(
			RelatorioAnaEvoInternacaoVO vo, Boolean emergencia)
			throws ApplicationBusinessException;

}