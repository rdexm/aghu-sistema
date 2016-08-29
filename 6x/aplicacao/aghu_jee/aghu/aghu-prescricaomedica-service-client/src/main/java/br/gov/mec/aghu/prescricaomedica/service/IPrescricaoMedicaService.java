package br.gov.mec.aghu.prescricaomedica.service;


import java.util.List;

import javax.ejb.Local;

import br.gov.mec.aghu.prescricaomedica.vo.AltaSumariaVO;
import br.gov.mec.aghu.prescricaomedica.vo.MpmAltaSumarioConcluidoVO;
import br.gov.mec.aghu.prescricaomedica.vo.MpmAltaSumarioServiceVO;
import br.gov.mec.aghu.prescricaomedica.vo.MpmPrescricaoMedicaVO;
import br.gov.mec.aghu.prescricaomedica.vo.PostoSaude;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.service.ServiceBusinessException;
import br.gov.mec.aghu.service.ServiceException;

@Local
@Deprecated
public interface IPrescricaoMedicaService {

	List<PostoSaude> listarMpmPostoSaudePorSeqDescricao(final Object parametro) throws ServiceException;
	
	Long listarMpmPostoSaudePorSeqDescricaoCount(final Object parametro) throws ServiceException;
	
	PostoSaude obterPostoSaudePorChavePrimaria(Integer seq) throws ServiceException;

	/**
	 * #34382 - Busca prescrição médica de um atendimento
	 * @param atdSeq
	 * @return
	 */
	List<MpmPrescricaoMedicaVO> obterPrescricaoTecnicaPorAtendimentoOrderCriadoEm(Integer seqAtendimento) throws ServiceException;
	
	/**
	 * #38994 - Serviço que retorna altas por numero da consulta
	 * @param conNumero
	 * @return
	 */
	AltaSumariaVO pesquisarAltaSumariosPorConsultaNumero(Integer conNumero) throws ServiceException;
	
	/**
	 * #39002 - Busca Ultima Prescricao Medica
	 * @param atdSeq
	 * @return
	 */
	MpmPrescricaoMedicaVO obterPrescricaoMedicaPorAtendimento(Integer atdSeq) throws ServiceException;
	
	/**
	 * #39007 - Serviço que retorna alta sumario por atendimento
	 * @param atdSeq
	 * @return
	 */
	MpmAltaSumarioServiceVO obterMpmAltaSumarioPorAtendimento(Integer atdSeq) throws ServiceException;
	
	/**
	 * #39010 - Busca alta de sumário concluído
	 * @param atdSeq
	 * @return
	 */
	MpmAltaSumarioConcluidoVO obterMpmAltaSumarioConcluidoPorAtendimento(Integer atdSeq) throws ServiceException;
	
	/**
	 * #39012 - Serviço para atualizar Sumario Alta apagando dados alta
	 * @param atdSeq
	 * @param nomeMicromputador
	 * @throws Exception 
	 */
	void atualizarSumarioAltaApagarDadosAlta(Integer atdSeq, String nomeMicromputador) throws ServiceException;
	
	/**
	 * #39013 - Serviço que estorna alta sumario
	 * @param seqp
	 * @param atdSeq
	 * @param apaSeq
	 * @param nomeMicrocomputador
	 * @throws ApplicationBusinessException
	 */
	void atualizarAltaSumarioEstorno(Short seqp, Integer atdSeq, Integer apaSeq, String nomeMicrocomputador) throws ServiceException;
	
	/**
	 * #39018  #39019 #39020 #39021 #39022 #39023 #39014 #39015 #39016
	 * Serviço que desbloqueia sumario alta
	 * @param atdSeq
	 * @param apaSeq
	 * @param seqp
	 * @param nomeMicrocomputador
	 * @throws ApplicationBusinessException
	 */
	void desbloquearSumarioAlta(Integer atdSeq, Integer apaSeq, Short seqp, String nomeMicrocomputador) throws ServiceException;
	
	/**
	 * #41077 - Serviço que retorna o profissional
	 * @param matricula
	 * @param vincodigo
	 * @return
	 * @throws ServiceException
	 */
	Object[] buscaConsProf(Integer matricula, Short vincodigo) throws ServiceException;
	
	Boolean verificarServidorMedico(final Integer serMatricula, final Short serCodigo) throws ServiceBusinessException, ServiceException;
	
}