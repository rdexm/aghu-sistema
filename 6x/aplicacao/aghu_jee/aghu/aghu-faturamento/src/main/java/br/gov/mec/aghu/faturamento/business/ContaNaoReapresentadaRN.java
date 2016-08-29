package br.gov.mec.aghu.faturamento.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.faturamento.dao.FatContasHospitalaresDAO;
import br.gov.mec.aghu.faturamento.vo.ContaNaoReapresentadaCPFVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ContaNaoReapresentadaRN extends BaseBusiness {

	private static final long serialVersionUID = -1268443613036521252L;

	private static final Log LOG = LogFactory.getLog(ContaNaoReapresentadaRN.class);
	
	@Inject
	private FatContasHospitalaresDAO fatContasHospitalaresDAO;

	@EJB
	private IParametroFacade parametroFacade;
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	private enum ContaNaoReapresentadaRNException implements BusinessExceptionCode {
		PARAMETRO_SEM_VALORES_CNR, NENHUM_REGISTRO_CNR;
	}
	
	//	Executar a consulta C1.
	//	Caso o parâmetro PS01 (utilizado na consulta) não retorne valor, a regra deverá ser interrompida e a mensagem MS02 deverá ser exibida.
	//	Os valores retornados em cada campo da consulta C1 deverão ser concatenados utilizando “;”  como separador entre os mesmos.
	//	Os dados retornados deverão ser gravados em um arquivo CSV nomeado como: CONTAS_NAO_REAPR_CPF_<mês competência><ano competência>.
	//	Onde: 
	//	o termo <mês competência> refere-se ao mês da competência selecionada na tela.
	//	o termo <ano competência> refere-se ao ano da competência selecionada na tela no formato ‘AA’ ex: 2014, no nome do arquivo ficaria 14.
	//	O cabeçalho (primeira linha a ser gravada no arquivo) deverá conter a seguinte informação:
	//	Conta;CPF;Reapresentada;Cod. SUS 
	//	Após geração do arquivo, exibir a mensagem MS03 com a quantidade de registros retornados e a quantidade de arquivos retornados.
	//	Caso a consulta C1 não retorne resultados exibir a mensagem MS04.
	//RN 01
	public List<ContaNaoReapresentadaCPFVO> buscarDadosContasNaoReapresentadasCPF(Integer ano, Integer mes, Date dataHrInicio) throws ApplicationBusinessException {
		List<ContaNaoReapresentadaCPFVO> lista = null;
		List<Long>cpfs = buscarCpfs();
		if(existeCpfs(cpfs)) {
			lista = fatContasHospitalaresDAO.buscarContasNaoReapresentadasCPF(cpfs, ano.shortValue(), mes.byteValue(), dataHrInicio);
			if(lista == null || lista.isEmpty()) {
				throw new ApplicationBusinessException(ContaNaoReapresentadaRNException.NENHUM_REGISTRO_CNR);
			}
		} else {
			throw new ApplicationBusinessException(ContaNaoReapresentadaRNException.PARAMETRO_SEM_VALORES_CNR, AghuParametrosEnum.P_CPF_DIF_N_REAPRE.toString());
		}
		return lista;
	}
	
	private List <Long> buscarCpfs() throws ApplicationBusinessException {
		String [] cpfsStr = parametroFacade.buscarValorArray(AghuParametrosEnum.P_CPF_DIF_N_REAPRE);
		if(cpfsStr == null || cpfsStr.length == 0){
			return null;
		}
		ArrayList<Long> cpfs = new ArrayList<Long>();
		for (String cpf : cpfsStr) {
			cpfs.add(Long.valueOf(cpf));
		}
		return cpfs;
	}
	
	private Boolean existeCpfs(List <Long> cpfs){
		return cpfs != null && !cpfs.isEmpty(); 
	}
	

}
