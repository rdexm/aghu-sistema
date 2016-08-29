package br.gov.mec.aghu.prescricaomedica.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmPostoSaude;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.AltaSumariaVO;
import br.gov.mec.aghu.prescricaomedica.vo.MpmAltaSumarioConcluidoVO;
import br.gov.mec.aghu.prescricaomedica.vo.MpmAltaSumarioServiceVO;
import br.gov.mec.aghu.prescricaomedica.vo.MpmPrescricaoMedicaVO;
import br.gov.mec.aghu.prescricaomedica.vo.PostoSaude;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.messages.MessagesUtils;
import br.gov.mec.aghu.service.ServiceBusinessException;
import br.gov.mec.aghu.service.ServiceException;

@Stateless
public class PrescricaoMedicaServiceImpl implements IPrescricaoMedicaService {

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject
    private MessagesUtils messagesUtils;
	
	/**
	 * Web Service #35759
	 */
	@Override
	public List<PostoSaude> listarMpmPostoSaudePorSeqDescricao(final Object parametro) throws ServiceException {
		
		List<MpmPostoSaude> postos =  this.prescricaoMedicaFacade.listarMpmPostoSaudePorSeqDescricao(parametro);
		
		// transforma para o tipo de retorno
		List<PostoSaude> listaRetorno = new ArrayList<PostoSaude>();
		for (MpmPostoSaude posto : postos) {
			PostoSaude ps = new PostoSaude();
			ps.setSeq(posto.getSeq());
			ps.setDescricao(posto.getDescricao());
			ps.setCidade(posto.getCidade().getNome());
			
			listaRetorno.add(ps);
		}
		
		return listaRetorno;
	}
	
	/**
	 * Web Service #35759
	 */
	@Override
	public Long listarMpmPostoSaudePorSeqDescricaoCount(final Object parametro) throws ServiceException {
		return this.prescricaoMedicaFacade.listarMpmPostoSaudePorSeqDescricaoCount(parametro);
	}
	
	@Override
	public PostoSaude obterPostoSaudePorChavePrimaria(Integer seq) throws ServiceException {
		
		MpmPostoSaude posto = this.prescricaoMedicaFacade.obterPostoSaudePorChavePrimaria(seq);
		
		PostoSaude ps = new PostoSaude();
		ps.setSeq(posto.getSeq());
		ps.setDescricao(posto.getDescricao());
		
		return ps;
	}


	/**
	 * #34382 - Busca prescrição médica de um atendimento
	 * @param atdSeq
	 * @return
	 */
	@Override
	public List<MpmPrescricaoMedicaVO> obterPrescricaoTecnicaPorAtendimentoOrderCriadoEm(final Integer seqAtendimento) throws ServiceException {
		List<MpmPrescricaoMedica> prescricoesMedicas =  this.prescricaoMedicaFacade.obterPrescricaoTecnicaPorAtendimentoOrderCriadoEm(seqAtendimento);
		
		// transforma para o tipo de retorno
		List<MpmPrescricaoMedicaVO> listaRetorno = new ArrayList<MpmPrescricaoMedicaVO>();
		for (MpmPrescricaoMedica prescricaoMedica : prescricoesMedicas) {
			MpmPrescricaoMedicaVO vo = new MpmPrescricaoMedicaVO();
			vo.setCriadoEm(prescricaoMedica.getCriadoEm());
			listaRetorno.add(vo);
		}
		
		return listaRetorno;	
	}
	
	
	/**
	 * #38994 - Serviço que retorna altas por numero da consulta
	 * @param conNumero
	 * @return
	 */
	@Override
	public AltaSumariaVO pesquisarAltaSumariosPorConsultaNumero(Integer conNumero) throws ServiceException {
		if (conNumero == null) {
			throw new ServiceException("Parametro conNumero esta nulo");
		}
		MpmAltaSumario altaSumaria = this.prescricaoMedicaFacade.pesquisarAltaSumariosPorNumeroConsulta(conNumero);
		AltaSumariaVO vo  = new AltaSumariaVO();
		if (altaSumaria != null) {
			vo.setDthrAlta(altaSumaria.getDthrAlta());
			if (altaSumaria.getTipo() != null) {
				vo.setTipo(altaSumaria.getTipo().getDescricao());
			}
		}
		return vo;
	}
	
	/**
	 * #39002 - Busca Ultima Prescricao Medica
	 * @param atdSeq
	 * @return
	 */
	@Override
	public MpmPrescricaoMedicaVO obterPrescricaoMedicaPorAtendimento(Integer atdSeq) throws ServiceException{
		if (atdSeq == null) {
			throw new ServiceException("Parametro atdSeq esta nulo");
		}
		MpmPrescricaoMedica prescricaoMedicao = this.prescricaoMedicaFacade.obterPrescricaoMedicaPorAtendimento(atdSeq);
		MpmPrescricaoMedicaVO vo = new MpmPrescricaoMedicaVO();
		if (prescricaoMedicao != null) {
			vo.setCriadoEm(prescricaoMedicao.getCriadoEm());
			vo.setMatriculaValida(prescricaoMedicao.getServidorValida() == null ? null : prescricaoMedicao.getServidorValida().getServidor().getId().getMatricula());
			vo.setMatriculaVincValida(prescricaoMedicao.getServidorValida() == null ? null : Integer.valueOf(prescricaoMedicao.getServidorValida().getServidor().getId().getVinCodigo()));
		}
		return vo;
	}
	
	/**
	 * #39007 - Serviço que retorna alta sumario por atendimento
	 * @param atdSeq
	 * @return
	 */
	@Override
	public MpmAltaSumarioServiceVO obterMpmAltaSumarioPorAtendimento(Integer atdSeq) throws ServiceException{
		try {
			MpmAltaSumario altaSumario = this.prescricaoMedicaFacade.obterMpmAltaSumarioPorAtendimento(atdSeq);
			MpmAltaSumarioServiceVO vo = null;
			if (altaSumario != null) {
				vo = new MpmAltaSumarioServiceVO();
				vo.setDthrAlta(altaSumario.getDthrAlta());
				vo.setTipo(altaSumario.getTipo() == null ? null : altaSumario.getTipo().getDescricao());
				vo.setRegistroSeq(altaSumario.getRegistro() == null ? null : altaSumario.getRegistro().getSeq());
				vo.setAtendimentoPacienteSeq(altaSumario.getId().getApaAtdSeq());
				vo.setAtendimentoSeq(altaSumario.getId().getApaAtdSeq());
				vo.setSeqp(altaSumario.getId().getSeqp());
			}
			
			return vo;
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	/**
	 * #39010 - Busca alta de sumário concluído
	 * @param atdSeq
	 * @return
	 */
	@Override
	public MpmAltaSumarioConcluidoVO obterMpmAltaSumarioConcluidoPorAtendimento(Integer atdSeq) throws ServiceException{
		try {
			MpmAltaSumario altaSumario = this.prescricaoMedicaFacade.obterMpmAltaSumarioConcluidoPorAtendimento(atdSeq);
			MpmAltaSumarioConcluidoVO vo = new MpmAltaSumarioConcluidoVO();
			if (altaSumario != null) {
				vo.setPacienteSeq(altaSumario.getId().getApaSeq());
				vo.setSeqp(altaSumario.getId().getSeqp());
			}
			return vo;
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	/**
	 * #39012 - Serviço para atualizar Sumario Alta apagando dados alta
	 * @param atdSeq
	 * @param nomeMicromputador
	 * @throws Exception 
	 */
	@Override
	public void atualizarSumarioAltaApagarDadosAlta(final Integer atdSeq, String nomeMicromputador) throws ServiceException{
		try {
			this.prescricaoMedicaFacade.atualizarSumarioAltaApagarDadosAlta(atdSeq, nomeMicromputador);
			} catch (ApplicationBusinessException e) {
				throw new ServiceException(e.getMessage());
			}
	}
	
	
	/**
	 * #39013 - Serviço que estorna alta sumario
	 * @param seqp
	 * @param atdSeq
	 * @param apaSeq
	 * @param nomeMicrocomputador
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void atualizarAltaSumarioEstorno(Short seqp, Integer atdSeq, Integer apaSeq, String nomeMicrocomputador) throws ServiceException{
		try {
			this.prescricaoMedicaFacade.atualizarAltaSumarioEstorno(seqp, atdSeq, apaSeq, nomeMicrocomputador);
			} catch (ApplicationBusinessException e) {
				throw new ServiceException(e.getMessage());
			}
	}
	
	/**
	 * #39018  #39019 #39020 #39021 #39022 #39023 #39014 #39015 #39016
	 * Serviço que desbloqueia sumario alta
	 * @param atdSeq
	 * @param apaSeq
	 * @param seqp
	 * @param nomeMicrocomputador
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void desbloquearSumarioAlta(Integer atdSeq, Integer apaSeq, Short seqp, String nomeMicrocomputador) throws ServiceException{
		try {
			this.prescricaoMedicaFacade.desbloquearSumarioAlta(atdSeq, apaSeq, seqp, nomeMicrocomputador);
		} catch (ApplicationBusinessException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	/**
	 * #41077 - Serviço que retorna o profissional
	 * @param matricula
	 * @param vincodigo
	 * @return
	 * @throws ServiceException
	 */
	@Override
	public Object[] buscaConsProf(Integer matricula,Short vincodigo) throws ServiceException {
		try {
			return this.prescricaoMedicaFacade.buscaConsProf(matricula, vincodigo);
		} catch (ApplicationBusinessException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	public Boolean verificarServidorMedico(final Integer serMatricula, final Short serCodigo) throws ServiceBusinessException, ServiceException{
		try {
			return this.prescricaoMedicaFacade.verificarServidorMedico(serMatricula, serCodigo);
		} catch (ApplicationBusinessException e) {
			throw new ServiceBusinessException(messagesUtils.getResourceBundleValue(e));
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}		
	}
}
