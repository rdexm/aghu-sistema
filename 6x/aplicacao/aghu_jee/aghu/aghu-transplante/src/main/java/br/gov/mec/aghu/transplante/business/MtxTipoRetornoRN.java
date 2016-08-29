package br.gov.mec.aghu.transplante.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.logging.Log;

import br.gov.mec.aghu.dominio.DominioTipoRetorno;
import br.gov.mec.aghu.model.MtxItemPeriodoRetorno;
import br.gov.mec.aghu.model.MtxTipoRetorno;
import br.gov.mec.aghu.model.MtxTransplantes;
import br.gov.mec.aghu.transplante.dao.MtxItemPeriodoRetornoDAO;
import br.gov.mec.aghu.transplante.dao.MtxTipoRetornoDAO;
import br.gov.mec.aghu.transplante.dao.MtxTransplantesDAO;
import br.gov.mec.aghu.transplante.vo.AgendaTransplanteRetornoVO;
import br.gov.mec.aghu.transplante.vo.TotalizadorAgendaTransplanteVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

@Stateless
public class MtxTipoRetornoRN extends BaseBusiness {

	private static final long serialVersionUID = -5553288417265871288L;
	
	private static final String PREV_MARCADA_NAO_COMPARECEU = "red";
	private static final String PREV_MARCADA_SEM_PREVISAO = "yellow";
	private static final String PREV_MARCADA_COM_PREVISAO = "blue";
	private static final String PREV_APENAS_PREVISAO = "orange";

	@Inject	
	private MtxTipoRetornoDAO mtxTipoRetornoDAO;
	
	@Inject
	private MtxItemPeriodoRetornoDAO mtxItemPeriodoRetornoDAO;
	
	@Inject
	private MtxTransplantesDAO mtxTransplantesDAO;
	
	@Override
	protected Log getLogger() {
		return null;
	}
	
	private enum MtxTipoRetornoRNExceptionCode implements BusinessExceptionCode {
		REGISTRO_JA_EXISTE_TIPO_RETORNO;
	}
	
	public void checarSeJaExiste(MtxTipoRetorno mtxTipoRetorno) throws ApplicationBusinessException {
		List<MtxTipoRetorno> lista = mtxTipoRetornoDAO.pesquisarTipoRetorno(mtxTipoRetorno, true);
		if(lista.size() > 0){
			throw new ApplicationBusinessException(MtxTipoRetornoRNExceptionCode.REGISTRO_JA_EXISTE_TIPO_RETORNO, Severity.ERROR);
		}
	}
	
	/**
	 * Faz o cálculo da previsão de retorno de cada um dos elementos da lista informada
	 * 
	 * #49925
	 * @param listaAgenda
	 * @param tipoRetorno
	 * @param descricaoTipoRetorno
	 * @return
	 */
	public List<AgendaTransplanteRetornoVO> calcularPrevisaoRetorno(List<AgendaTransplanteRetornoVO> listaAgenda, DominioTipoRetorno tipoRetorno, MtxItemPeriodoRetorno descricaoTipoRetorno) {
		List<AgendaTransplanteRetornoVO> listaPrevisaoConsulta = new ArrayList<AgendaTransplanteRetornoVO>();
		List<AgendaTransplanteRetornoVO> listaRetorno = null;

		List<MtxItemPeriodoRetorno> listaItemPeriodo = mtxItemPeriodoRetornoDAO.pesquisarItemPeriodoRetorno(tipoRetorno, descricaoTipoRetorno.getPeriodoRetorno().getSeq(), null);
		
		for (AgendaTransplanteRetornoVO agendaTransplanteRetornoVO : listaAgenda) {
			if (agendaTransplanteRetornoVO.getIndAusente()){
				agendaTransplanteRetornoVO.setCorLegenda(PREV_MARCADA_NAO_COMPARECEU);
			}
			else if (StringUtils.isEmpty(agendaTransplanteRetornoVO.getCorLegenda())){
				// toda consulta que não tiver sinalização ainda é marcada como sem previsão, 
				// até que calcule a previsão e verifique que a marcação coincide com a previsão 
				agendaTransplanteRetornoVO.setCorLegenda(PREV_MARCADA_SEM_PREVISAO);
			}

			if (DominioTipoRetorno.A.equals(tipoRetorno)){
				this.calcularPrevisaoPaciente(agendaTransplanteRetornoVO, agendaTransplanteRetornoVO.getDataIngresso(), listaPrevisaoConsulta, listaItemPeriodo);
			} else if (DominioTipoRetorno.D.equals(tipoRetorno)){
				if (agendaTransplanteRetornoVO.getDataOcorrenciaTransplante() != null){
					this.calcularPrevisaoPaciente(agendaTransplanteRetornoVO, agendaTransplanteRetornoVO.getDataOcorrenciaTransplante(), listaPrevisaoConsulta, listaItemPeriodo);
				}
			}
			else if (DominioTipoRetorno.X.equals(tipoRetorno)){
				this.calcularPrevisaoPaciente(agendaTransplanteRetornoVO, agendaTransplanteRetornoVO.getDataIngresso(), listaPrevisaoConsulta, listaItemPeriodo);

				if (agendaTransplanteRetornoVO.getDataOcorrenciaTransplante() != null){
					this.calcularPrevisaoPaciente(agendaTransplanteRetornoVO, agendaTransplanteRetornoVO.getDataOcorrenciaTransplante(), listaPrevisaoConsulta, listaItemPeriodo);
				}
			}
		}
		
		listaRetorno = this.consolidarListaConsulta(listaAgenda, listaPrevisaoConsulta);

		return listaRetorno;
	}
	
	/**
	 * Mescla as listas de consultas marcadas com a lista de previsões geradas
	 * @param listaAgenda
	 * @param listaPrevisao
	 * @return
	 */
	private List<AgendaTransplanteRetornoVO> consolidarListaConsulta(List<AgendaTransplanteRetornoVO> listaAgenda,  List<AgendaTransplanteRetornoVO> listaPrevisao){
		if (listaPrevisao.size() > 0){
			int posicaoConsulta = 0;
			List<AgendaTransplanteRetornoVO> listaAgendaData = null;
			List<AgendaTransplanteRetornoVO> listaTemp = null;
			boolean encontrou = false;
			
			for (AgendaTransplanteRetornoVO agendaTransplanteRetornoVO : listaPrevisao) {
				encontrou = false;
				listaTemp = listaAgenda;

				do {
					posicaoConsulta = listaTemp.indexOf(agendaTransplanteRetornoVO);

					// Caso já exista consulta marcada para o paciente na data em questão, sinaliza que a consulta foi marcada na data da revisão
					if (posicaoConsulta >= 0){
						encontrou = true;
						
						// Somente se não for uma previsão
						if (!listaTemp.get(posicaoConsulta).getPrevisao()){
							listaTemp.get(posicaoConsulta).setCorLegenda(PREV_MARCADA_COM_PREVISAO);
						}
						
						if ((posicaoConsulta + 1) < (listaTemp.size() - 1)) {
							listaTemp = listaTemp.subList(posicaoConsulta + 1, listaTemp.size() - 1);
						}
						else{
							break;
						}
					}
				} while (posicaoConsulta >=0);

				if (!encontrou){
					// Caso não exista, encaixa a previsão naquela data
					listaAgendaData = this.obterConsultasParaData(listaAgenda, agendaTransplanteRetornoVO.getDataConsultaSemHora());
					agendaTransplanteRetornoVO.setDataConsulta(this.encaixarPrevisaoNaAgendaDia(listaAgendaData, agendaTransplanteRetornoVO.getDataConsultaSemHora()));
					agendaTransplanteRetornoVO.setPrevisao(true);
					listaAgenda.add(agendaTransplanteRetornoVO);
				}
			}
		}
		
		Collections.sort(listaAgenda, AgendaTransplanteRetornoVO.DataConsultaComparator);
		
		return listaAgenda;
	}
	
	/**
	 * Busca a data para encaixar a previsão na agenda de uma data
	 * #49925
	 * @param listaAgendaData
	 * @param dataConsulta
	 * @return
	 */
	private Date encaixarPrevisaoNaAgendaDia(List<AgendaTransplanteRetornoVO> listaAgendaData, Date dataConsulta){
		// Deve-se achar o primeiro horário disponível na agenda da data, verificando de 30 em 30 minutos a partir de 8hrs
		int minutosIncrementar = 30;
		
		Calendar dataRetorno = Calendar.getInstance();
		dataRetorno.setTime(dataConsulta);
		dataRetorno.set(Calendar.HOUR_OF_DAY, 8);
		dataRetorno.set(Calendar.MINUTE, 0);
		dataRetorno.set(Calendar.SECOND, 0);
		dataRetorno.set(Calendar.MILLISECOND, 0);
		
		Calendar dataFimTeste = Calendar.getInstance();
		dataFimTeste.setTime(dataRetorno.getTime());
		dataFimTeste.set(Calendar.MINUTE, minutosIncrementar);
		
		for (AgendaTransplanteRetornoVO agendaTransplanteRetornoVO : listaAgendaData) {
			// Caso encontre um horário na agenda que conflite com a previsão, avança 30 min
			// Ao terminar o loop, já vai ter o horário adequado
			if (dataRetorno.getTime().compareTo(agendaTransplanteRetornoVO.getHoraDataConsulta()) <= 0 
					&& dataFimTeste.getTime().compareTo(agendaTransplanteRetornoVO.getHoraDataConsulta()) > 0){
		
				dataRetorno.add(Calendar.MINUTE, minutosIncrementar);
				dataFimTeste.add(Calendar.MINUTE, minutosIncrementar);
			} 
		}
		
		return dataRetorno.getTime();
	}
	
	/**
	 * Retorna a lista das consultas apenas para a data informada
	 * #49925
	 * @param listaAgenda
	 * @param dataConsulta
	 * @return
	 */
	private List<AgendaTransplanteRetornoVO> obterConsultasParaData(List<AgendaTransplanteRetornoVO> listaAgenda, Date dataConsulta){
		List<AgendaTransplanteRetornoVO> listaRetorno = new ArrayList<AgendaTransplanteRetornoVO>();

		for (AgendaTransplanteRetornoVO agendaTransplanteRetornoVO : listaAgenda) {
			if (agendaTransplanteRetornoVO.getDataConsultaSemHora().equals(dataConsulta)){
				listaRetorno.add(agendaTransplanteRetornoVO);
			}
		}
		
		Collections.sort(listaRetorno, AgendaTransplanteRetornoVO.DataConsultaComparator);
		
		return listaRetorno;
	}
	
	/**
	 * Faz o cálculo para as previsões de retorno a partir de uma consulta de um paciente
	 * 
	 * #49925
	 * @param agendaPaciente
	 * @param dataInicial
	 * @param listaPrevisao
	 * @param listaItemPeriodo
	 */
	private void calcularPrevisaoPaciente(AgendaTransplanteRetornoVO agendaPaciente, Date dataInicial, List<AgendaTransplanteRetornoVO> listaPrevisao, List<MtxItemPeriodoRetorno> listaItemPeriodo){
		Calendar dataReferencia = Calendar.getInstance();
		dataReferencia.setTime(dataInicial);
		AgendaTransplanteRetornoVO agendaPacientePrevisao = null;
		
		for (MtxItemPeriodoRetorno mtxItemPeriodoRetorno : listaItemPeriodo) {
			for (int i = 1; i <= mtxItemPeriodoRetorno.getQuantidade(); i++) {
				dataReferencia.add(Calendar.DATE, mtxItemPeriodoRetorno.getIndRepeticao().getQuantidadeDias());
				dataReferencia = retornarDataUtil(dataReferencia);
				
				// Realiza uma cópia do objeto sendo previsionado para preencher a data da consulta
				agendaPacientePrevisao = (AgendaTransplanteRetornoVO) SerializationUtils.clone(agendaPaciente);
				agendaPacientePrevisao.setDataConsulta(dataReferencia.getTime());
				agendaPacientePrevisao.setCorLegenda(PREV_APENAS_PREVISAO);
				
				// Adiciona apenas se ainda não existir uma previsão para o paciente naquele dia 
				if (!listaPrevisao.contains(agendaPacientePrevisao)){
					listaPrevisao.add(agendaPacientePrevisao);
				}
			}
		}
	}
	

	/**
	 * Retorna a data do próximo dia útil, caso não seja a própria data
	 * #49925
	 * @param data
	 * @return
	 */
	private Calendar retornarDataUtil(Calendar data){
		while (data.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
				data.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
			data.add(Calendar.DATE, 1);
		}
		
		return data;
	}
	
	/**
	 * Obtem o total de consultas marcadas e previstas por cada dia
	 * #49925
	 * @param listaAgenda
	 * @param descricaoTipoRetorno
	 * @return
	 */
	public List<TotalizadorAgendaTransplanteVO> obterTotalConsultasPorDia(List<AgendaTransplanteRetornoVO> listaAgenda, MtxItemPeriodoRetorno descricaoTipoRetorno) {
		List<TotalizadorAgendaTransplanteVO> listaRetorno = new ArrayList<TotalizadorAgendaTransplanteVO>();
		
		Collections.sort(listaAgenda, AgendaTransplanteRetornoVO.DataConsultaComparator);
		
		Calendar dataInicial = Calendar.getInstance();
		dataInicial.setTime(listaAgenda.get(0).getDataConsultaSemHora());
		Date dataFim = listaAgenda.get(listaAgenda.size() -1).getDataConsultaSemHora();

		Integer totalPorData = 0;
		
		while (dataInicial.getTime().compareTo(dataFim) <= 0) {
			totalPorData = this.obterConsultasParaData(listaAgenda, dataInicial.getTime()).size();

			if (totalPorData > 0){
				listaRetorno.add(new TotalizadorAgendaTransplanteVO(dataInicial.getTime(), descricaoTipoRetorno.getPeriodoRetorno().getTipoRetorno().getDescricao(), totalPorData));
			}
			
			dataInicial.add(Calendar.DATE, 1);
			dataInicial.set(Calendar.HOUR_OF_DAY, 0);
			dataInicial.set(Calendar.MINUTE, 0);
			dataInicial.set(Calendar.SECOND, 0);
		}
		
		
		return listaRetorno;
	}

	/**
	 * Realiza a atualização da observação do transplante
	 * #49925
	 * @param observacaoTransplante
	 * @return
	 */
	public void atualizarObservacaoTransplante(MtxTransplantes observacaoTransplante) {
		MtxTransplantes transplante = mtxTransplantesDAO.obterOriginal(observacaoTransplante.getSeq());
		
		transplante.setObservacoes(observacaoTransplante.getObservacoes());
		
		this.mtxTransplantesDAO.atualizar(transplante);
	}
}
