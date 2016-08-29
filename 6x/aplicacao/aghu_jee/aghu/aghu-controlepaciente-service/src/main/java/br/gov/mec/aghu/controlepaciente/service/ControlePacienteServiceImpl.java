package br.gov.mec.aghu.controlepaciente.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.controlepaciente.business.IControlePacienteFacade;
import br.gov.mec.aghu.controlepaciente.cadastrosbasicos.business.ICadastrosBasicosControlePacienteFacade;
import br.gov.mec.aghu.controlepaciente.vo.DadosSinaisVitaisVO;
import br.gov.mec.aghu.controlepaciente.vo.EcpItemControleVO;
import br.gov.mec.aghu.controlepaciente.vo.RegistroControlePacienteServiceVO;
import br.gov.mec.aghu.controlepaciente.vo.RegistroControlePacienteVO;
import br.gov.mec.aghu.dominio.DominioTipoGrupoControle;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.EcpControlePaciente;
import br.gov.mec.aghu.model.EcpItemControle;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.messages.MessagesUtils;
import br.gov.mec.aghu.service.ServiceBusinessException;
import br.gov.mec.aghu.service.ServiceException;

@Stateless
public class ControlePacienteServiceImpl implements IControlePacienteService {

	@EJB
	private IControlePacienteFacade controlePacienteFacade;
	@EJB
	private ICadastrosBasicosControlePacienteFacade cadastrosBasicosControlePacienteFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	private static final Log LOG = LogFactory.getLog(ControlePacienteServiceImpl.class);
	
	@Inject
	private MessagesUtils messagesUtils;

	/**
	 * Web Service #35809
	 */
	@Override
	public List<DadosSinaisVitaisVO> pesquisarUltimosSinaisVitaisPeloCodigoPaciente(Integer pacCodigo) throws ServiceException, ApplicationBusinessException {
		List<DadosSinaisVitaisVO> result = new ArrayList<DadosSinaisVitaisVO>();
		List<EcpControlePaciente> controles = controlePacienteFacade.obterUltimosDadosControlePacientePelaCodigoPaciente(pacCodigo);
		for (EcpControlePaciente controle : controles) {
			DadosSinaisVitaisVO sinaisVitais = new DadosSinaisVitaisVO(controle.getItem().getDescricao(), controle.getItem().getSigla(), 
					controle.getHorario().getDataHora(), controle.getMedicao(), controle.getMedicaoFormatada(), controle.getUnidadeMedida() != null ? controle.getUnidadeMedida().getDescricao() : null);
			result.add(sinaisVitais);
		}
		return result;
	}

	/**
	 * Web Service #35602
	 */
	@Override
	public List<DadosSinaisVitaisVO> pesquisarUltimosSinaisVitaisPelaConsulta(Integer consulta) throws ServiceException, ApplicationBusinessException {
		List<DadosSinaisVitaisVO> result = new ArrayList<DadosSinaisVitaisVO>();
		List<EcpControlePaciente> controles = controlePacienteFacade.obterUltimosDadosControlePacientePelaConsulta(consulta);
		for (EcpControlePaciente controle : controles) {
			DadosSinaisVitaisVO sinaisVitais = new DadosSinaisVitaisVO(controle.getItem().getDescricao(), controle.getItem().getSigla(), 
					controle.getHorario().getDataHora(), controle.getMedicao(), controle.getMedicaoFormatada(),
					controle.getUnidadeMedida() != null ? controle.getUnidadeMedida().getDescricao() : null);
			result.add(sinaisVitais);
		}
		return result;
	}
	
	@Override
	public List<DadosSinaisVitaisVO> pesquisarDadosSinaisVitais(List<Short> listSeqs) throws ServiceException, ApplicationBusinessException {
		List<DadosSinaisVitaisVO> result = new ArrayList<DadosSinaisVitaisVO>();
		List<EcpItemControle> controles = controlePacienteFacade.pesquisarDadosSinaisVitais(listSeqs);
		for (EcpItemControle controle : controles) {
			DadosSinaisVitaisVO sinaisVitais = new DadosSinaisVitaisVO(controle.getSeq(), controle.getDescricao(), controle.getSigla());
			result.add(sinaisVitais);
		}
		return result;
	}

	@Override
	public List<DadosSinaisVitaisVO> pesquisarSinaisVitaisAtivosPorSiglaDescricaoSeqGrupo(String strPesquisa, Integer seqGrupo, Integer maxResults) {
		List<DadosSinaisVitaisVO> result = new ArrayList<DadosSinaisVitaisVO>();
		List<EcpItemControle> controles = controlePacienteFacade.pesquisarSinaisVitaisAtivosPorSiglaDescricaoSeqGrupo(strPesquisa, seqGrupo, maxResults);
		for (EcpItemControle controle : controles) {
			Integer ummSeq = (controle.getUnidadeMedidaMedica() != null ? controle.getUnidadeMedidaMedica().getSeq() : null);
			DadosSinaisVitaisVO sinaisVitais = new DadosSinaisVitaisVO(controle.getSeq(), controle.getDescricao(), controle.getSigla(), ummSeq);
			result.add(sinaisVitais);
		}
		return result;
	}

	@Override
	public Long pesquisarSinaisVitaisAtivosPorSiglaDescricaoSeqGrupoCount(String strPesquisa, Integer seqGrupo) {
		return controlePacienteFacade.pesquisarSinaisVitaisAtivosPorSiglaDescricaoSeqGrupoCount(strPesquisa, seqGrupo);
	}
	
	/**
	 * Web Service #35799
	 */
	@Override
	public Boolean verificarExisteSinalVitalPorPaciente(Short iceSeq, Integer pacCodigo) throws ServiceException {
		return this.controlePacienteFacade.verificarExisteSinalVitalPorPaciente(iceSeq, pacCodigo);
	}
	
	/**
	 * Web Service #35799
	 */
	@Override
	public String obterDescricaoItemControle(Short iceSeq) throws ServiceException {
		return this.controlePacienteFacade.obterDescricaoItemControle(iceSeq);
	}
	
	@Override
	public List<EcpItemControleVO> buscarItensControlePorPacientePeriodo(
			Integer pacCodigo, Long trgSeq) {
		List<EcpItemControleVO> retorno = new ArrayList<EcpItemControleVO>();
		AipPacientes paciente = pacienteFacade.buscaPaciente(pacCodigo);
		List<EcpItemControle> itens = this.cadastrosBasicosControlePacienteFacade.buscarItensControlePorPacientePeriodo(paciente, null, null, trgSeq, DominioTipoGrupoControle.MN);
		
		for(EcpItemControle item : itens) {
			EcpItemControleVO vo = new EcpItemControleVO(item.getSeq(), item.getSigla(), item.getDescricaoUnidadeMedicaGrupo());
			retorno.add(vo);
		}
		
		return retorno;
	}
	
	@Override
	public List<RegistroControlePacienteServiceVO> pesquisarRegistrosPaciente(
			Integer pacCodigo, List<EcpItemControleVO> listaItensControle,
			Long trgSeq) throws ApplicationBusinessException {
		AipPacientes paciente = pacienteFacade.buscaPaciente(pacCodigo);
		List<EcpItemControle> itensControle = new ArrayList<EcpItemControle>();
		for(EcpItemControleVO item : listaItensControle) {
			EcpItemControle temp = new EcpItemControle();
			temp.setSeq(item.getSeq());
			itensControle.add(temp);
		}
		List<RegistroControlePacienteVO> lista = this.controlePacienteFacade.pesquisarRegistrosPaciente(null, paciente, null, null, null, itensControle, trgSeq);
		
		List<RegistroControlePacienteServiceVO> retorno = new ArrayList<RegistroControlePacienteServiceVO>();
		
		for(RegistroControlePacienteVO item : lista) {
			RegistroControlePacienteServiceVO vo = new RegistroControlePacienteServiceVO(
					item.getHorarioSeq(), item.getDataHoraMedicao(), item.getAnotacoesHorario(),
					item.getAtdSeq(), item.getTrgSeq(), item.getValor(), item.getLimite(), item.getLocalAtendimento(),
					item.getPacCodigo(), item.getUnfSeq(), item.isRenderizarAnotacoes(), item.getAnotacoes());
			retorno.add(vo);
		}
		
		return retorno;
	}
	
	@Override
	public boolean validaUnidadeFuncionalInformatizada(Short unfSeq) throws ApplicationBusinessException {
		return this.controlePacienteFacade.validaUnidadeFuncionalInformatizada(null, unfSeq);
	}
	
	@Override
	public Boolean verificarExisteSinalVitalPorPaciente(Integer pacCodigo) throws ServiceException {
		return this.controlePacienteFacade.verificarExisteSinalVitalPorPaciente(pacCodigo);
	}
	
	@Override
	public void excluirRegistroControlePaciente(Long seqHorarioControle) throws ServiceException, ApplicationBusinessException {
		try {
			this.controlePacienteFacade.excluirRegistroControlePaciente(seqHorarioControle);
		} catch (BaseException e) {
			LOG.error("Erro ao excluir controle de paciente.", e);
			String message = messagesUtils.getResourceBundleValue(e);
			throw new ServiceBusinessException(message);
		}
		
	}
	
	@Override
	public void atualizarAtdSeqHorarioControlePaciente(Integer atdSeq, Long trgSeq) throws ServiceException {
		try {
			this.controlePacienteFacade.atualizarAtdSeqHorarioControlePaciente(atdSeq, trgSeq);
		} catch (BaseException e) {
			LOG.error("Erro ao atualizar o atendimento para os controles do paciente.", e);
			String message = messagesUtils.getResourceBundleValue(e);
			throw new ServiceBusinessException(message);
		}
		
	}
}
