package br.gov.mec.aghu.internacao.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.DadosInternacaoUrgenciaVO;
import br.gov.mec.aghu.internacao.vo.LeitoVO;
import br.gov.mec.aghu.internacao.vo.UnidadeFuncional;
import br.gov.mec.aghu.internacao.vo.UnidadeFuncionalFiltro;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.messages.MessagesUtils;
import br.gov.mec.aghu.service.ServiceBusinessException;
import br.gov.mec.aghu.service.ServiceException;

@Stateless
public class InternacaoServiceImpl implements IInternacaoService {
	
	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@Inject
    private MessagesUtils messagesUtils;
	
	@Override
	public String obterNomeInstituicaoHospitalar() throws ServiceException {
		return cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
	}

	@Override
	public List<UnidadeFuncional> pesquisarUnidadeFuncional(
			UnidadeFuncionalFiltro filtro) throws ServiceException {
		String strPesquisa = null;

		if (filtro.getSeq() != null) {
			strPesquisa = filtro.getSeq().toString();
		} else {
			strPesquisa = filtro.getDescricao();
		}
		
		List<AghUnidadesFuncionais> unidades = this.cadastrosBasicosInternacaoFacade.pesquisarUnidadeFuncionalPorCodigoEDescricao(strPesquisa);

		// transforma para o tipo de retorno
		List<UnidadeFuncional> result = new ArrayList<UnidadeFuncional>();
		for (AghUnidadesFuncionais au : unidades) {
			UnidadeFuncional u = new UnidadeFuncional(au.getSeq(),
					au.getDescricao(), au.getSigla());
			result.add(u);
		}

		// TODO implementar os outros possiveis filtros - indSitUnidFunc

		return result;
	}
	
	
	@Override
	public List<UnidadeFuncional> pesquisarUnidadeFuncional(
			UnidadeFuncionalFiltro filtro, Integer maxResults) throws ServiceException {
		String strPesquisa = null;

		if (filtro.getSeq() != null) {
			strPesquisa = filtro.getSeq().toString();
		} else {
			strPesquisa = filtro.getDescricao();
		}

		
		List<AghUnidadesFuncionais> unidades =
				this.aghuFacade.pesquisarUnidadeFuncionalVOPorCodigoEDescricao(strPesquisa, maxResults);

		// transforma para o tipo de retorno
		List<UnidadeFuncional> result = new ArrayList<UnidadeFuncional>();
		for (AghUnidadesFuncionais au : unidades) {
			UnidadeFuncional u = new UnidadeFuncional(au.getSeq(),
					au.getDescricao(), au.getSigla());
			result.add(u);
		}

		// TODO implementar os outros possiveis filtros - indSitUnidFunc

		return result;
	}
	
	@Override
	public Long pesquisarUnidadeFuncionalCount(UnidadeFuncionalFiltro filtro) throws ServiceException {
		String strPesquisa = null;

		if (filtro.getSeq() != null) {
			strPesquisa = filtro.getSeq().toString();
		} else {
			strPesquisa = filtro.getDescricao();
		}

		
		return this.aghuFacade.pesquisarUnidadeFuncionalVOPorCodigoEDescricaoCount(strPesquisa);
		
	}
	
	/**
	 * Web Service #35685 e Web Service #34369
	 */
	@Override
	public Short pesquisarUnidadeFuncionalTriagemRecepcao(
			List<Short> listaUnfSeqTriagemRecepcao, Short unfSeqMicroComputador) throws ServiceException {

		return this.cadastrosBasicosInternacaoFacade
				.pesquisarUnidadeFuncionalTriagemRecepcao(listaUnfSeqTriagemRecepcao, unfSeqMicroComputador);
	}

	@Override
	public Integer obterSeqAtendimentoUrgenciaPorConsulta(Integer conNumero) throws ServiceException {
		if (conNumero == null) {
			throw new ServiceException("Parâmetro obrigatório");
		}
		try {
			return internacaoFacade.obterSeqAtendimentoUrgenciaPorConsulta(conNumero);
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	public DadosInternacaoUrgenciaVO obterInternacaoPorAtendimentoUrgencia(Integer atuSeq) throws ServiceException {
		if (atuSeq == null) {
			throw new ServiceException("Parâmetro obrigatório");
		}
		try {
			DadosInternacaoUrgenciaVO dadosInternacao = null;
			AinInternacao internacao = internacaoFacade.obterInternacaoPorAtendimentoUrgencia(atuSeq);
			if (internacao != null) {
				dadosInternacao = new DadosInternacaoUrgenciaVO();
				dadosInternacao.setSeq(internacao.getSeq());
				if (internacao.getServidorProfessor() != null) {
					dadosInternacao.setMatriculaProfessor(internacao.getServidorProfessor().getId().getMatricula());
					dadosInternacao.setVinCodigoProfessor(internacao.getServidorProfessor().getId().getVinCodigo());
				} else {
					dadosInternacao.setMatriculaProfessor(null);
					dadosInternacao.setVinCodigoProfessor(null);
				}
			}
			return dadosInternacao;
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public void atualizarServidorProfessorInternacao(Integer seqInternacao, Integer matriculaProfessor, Short vinCodigoProfessor, String nomeMicrocomputador,
			Integer matriculaServidorLogado, Short vinCodigoServidorLogado) throws ServiceException {
		String mensagem = internacaoFacade.atualizarServidorProfessorInternacao(seqInternacao, matriculaProfessor, vinCodigoProfessor, nomeMicrocomputador,
				matriculaServidorLogado, vinCodigoServidorLogado);
		if (StringUtils.isNotBlank(mensagem)) {
			throw new ServiceException(mensagem);
		}
	}

	@Override
	public Boolean verificarPacienteInternadoPorConsulta(Integer numeroConsulta) throws ServiceException {
		try {
			return internacaoFacade.verificarPacienteInternadoPorConsulta(numeroConsulta);
		} catch (ApplicationBusinessException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public Boolean verificarPacienteIngressoSOPorConsulta(Integer numeroConsulta) throws ServiceException {
		try {
			return internacaoFacade.verificarPacienteIngressoSOPorConsulta(numeroConsulta);
		} catch (ApplicationBusinessException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	public void finalizarConsulta(Integer numeroConsulta, Integer codigoPaciente, Integer matricula, Short vinCodigo, String hostName) throws ServiceBusinessException, ServiceException{
		try {
			internacaoFacade.finalizarConsulta(numeroConsulta, codigoPaciente, matricula, vinCodigo, hostName);
		} catch (ApplicationBusinessException e) {
			throw new ServiceBusinessException(messagesUtils.getResourceBundleValue(e));
		} catch (BaseException e){
			throw new ServiceBusinessException(messagesUtils.getResourceBundleValue(e));
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}		
	}

	@Override
	public void realizarInternacaoPacienteAutomaticamente(Integer matricula, Short vinCodigo, Integer pacCodigo, Short seqp, Integer numeroConsulta, String hostName, Long trgSeq) throws ServiceBusinessException, ServiceException {
		try {			
			internacaoFacade.realizarInternacaoPacienteAutomaticamente(matricula, vinCodigo, pacCodigo, seqp, numeroConsulta, hostName, trgSeq);			
		} catch (ApplicationBusinessException e) {
			throw new ServiceBusinessException(messagesUtils.getResourceBundleValue(e));
		} catch (BaseException e) {
			throw new ServiceBusinessException(messagesUtils.getResourceBundleValue(e));
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}			
	}

	@Override
	public Boolean verificarLeitoExiste(String idLeito) throws ServiceException {
		try{
			return internacaoFacade.verificarLeitoExiste(idLeito);
		} catch(RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public List<LeitoVO> pesquisarLeitosPorSeqUnf(List<Short> unfs, String leito) throws ServiceException {
		List<AinLeitos> leitos = null;
		List<LeitoVO> resultado = new ArrayList<LeitoVO>();
		try{
			leitos = internacaoFacade.pesquisarLeitosPorSeqUnf(unfs,leito);
			if(leitos == null || leitos.isEmpty()) {
				return resultado;
			}
			for (AinLeitos item : leitos) {
				resultado.add(new LeitoVO(item.getLeitoID(), item.getLeito(), item.getIndSituacao().getDescricao(), 
				item.getUnidadeFuncional().getSeq()));
			}
		} catch(RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
		return resultado;
	}

	@Override
	public Long pesquisarLeitosPorSeqUnfCount(List<Short> unfs, String leito)
			throws ServiceException {
		try{
			return 	internacaoFacade.pesquisarLeitosPorSeqUnfCount(unfs,leito);
		} catch(RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}
}
