package br.gov.mec.aghu.perinatologia.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.configuracao.service.IConfiguracaoService;
import br.gov.mec.aghu.configuracao.vo.UnidadeFuncionalVO;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.exames.service.IExamesService;
import br.gov.mec.aghu.exames.vo.ExameMaterialAnaliseVO;
import br.gov.mec.aghu.exames.vo.ExameSignificativoVO;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.service.ServiceException;

@Stateless
public class ManterCadastroExamesSignificativosRN extends BaseBusiness {
	private static final String SERVICE = "SERVICE: ";

	private static final long serialVersionUID = 9070291774629755296L;

	private static final Log LOG = LogFactory.getLog(ManterCadastroExamesSignificativosRN.class);
	
	@Inject
	IConfiguracaoService configuracaoService;

	@Inject
	IExamesService examesService;

	@Inject
	@QualificadorUsuario
	private Usuario usuario;
	
	private enum ManterCadastroExamesSignificativosRNExceptionCode implements BusinessExceptionCode {
		MSG_SERVICO_INDISPONIVEL,
		MSG_ERRO_PERSISTIR_EXAME_SIGNIFICATIVO,
		MSG_ERRO_LISTAR_EXAME_SIGNIFICATIVO,
		MSG_ERRO_LISTAR_UNIDADE_FUNCIONAL,
		MSG_ERRO_LISTAR_EXAME_MATERIAL,
		MSG_ERRO_DELETAR_EXAME_SIGNIFICATIVO,
	}

	public List<ExameSignificativoVO> pesquisarExamesSignificativos(Short unfSeq, String siglaExame, Integer seqMatAnls, Boolean cargaExame, int firstResult,
			int maxResults) throws ApplicationBusinessException  {
		List<ExameSignificativoVO> listaExameSignificativoVO = new ArrayList<ExameSignificativoVO>();
		
		try {
 			listaExameSignificativoVO = this.examesService.pesquisarUnidadesFuncionaisExamesSignificativosPerinato(unfSeq, siglaExame, seqMatAnls, cargaExame, firstResult, maxResults);
		} catch (ServiceException e) {
			LOG.error(SERVICE + e.getMessage(), e);
			throw new ApplicationBusinessException(ManterCadastroExamesSignificativosRNExceptionCode.MSG_ERRO_LISTAR_EXAME_SIGNIFICATIVO);
		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new ApplicationBusinessException(ManterCadastroExamesSignificativosRNExceptionCode.MSG_SERVICO_INDISPONIVEL);
		}
		return listaExameSignificativoVO;
	}

	public Long pesquisarExamesSignificativosCount(Short unfSeq, String siglaExame, Integer seqMatAnls, Boolean cargaExame) throws ApplicationBusinessException {
		Long retorno = 0L;
		
		try {
			retorno = this.examesService.pesquisarUnidadesFuncionaisExamesSignificativosPerinatoCount(unfSeq, siglaExame, seqMatAnls, cargaExame);
		} catch (ServiceException e) {
			LOG.error(SERVICE + e.getMessage(), e);
			throw new ApplicationBusinessException(ManterCadastroExamesSignificativosRNExceptionCode.MSG_ERRO_LISTAR_EXAME_SIGNIFICATIVO); 
		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new ApplicationBusinessException(ManterCadastroExamesSignificativosRNExceptionCode.MSG_SERVICO_INDISPONIVEL); 
		}
		return retorno; 
	}

	public List<UnidadeFuncionalVO> pesquisarUnidadeFuncional(String descricao) throws ApplicationBusinessException  {
		List<UnidadeFuncionalVO> listaUnidadeFuncionalVO = new ArrayList<UnidadeFuncionalVO>();
		
		try {
			listaUnidadeFuncionalVO = this.configuracaoService.pesquisarAghUnidadesFuncionaisAtivasPorSeqOuDescricao(descricao);
		} catch (ServiceException e) {
			LOG.error(SERVICE + e.getMessage(), e);
			throw new ApplicationBusinessException(ManterCadastroExamesSignificativosRNExceptionCode.MSG_ERRO_LISTAR_UNIDADE_FUNCIONAL);
		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new ApplicationBusinessException(ManterCadastroExamesSignificativosRNExceptionCode.MSG_SERVICO_INDISPONIVEL);
		}
		return listaUnidadeFuncionalVO;
	}

	public Long pesquisarUnidadeFuncionalCount(String descricao) throws ApplicationBusinessException  {
		Long retorno = 0L;
		
		try {
			retorno = this.configuracaoService.pesquisarAghUnidadesFuncionaisAtivasPorSeqOuDescricaoCount(descricao);
		} catch (ServiceException e) {
			LOG.error(SERVICE + e.getMessage(), e);
			throw new ApplicationBusinessException(ManterCadastroExamesSignificativosRNExceptionCode.MSG_ERRO_LISTAR_UNIDADE_FUNCIONAL); 
		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new ApplicationBusinessException(ManterCadastroExamesSignificativosRNExceptionCode.MSG_SERVICO_INDISPONIVEL); 
		}
		return retorno; 
	}

	public List<ExameMaterialAnaliseVO> pesquisarExameMaterial(String descricao) throws ApplicationBusinessException  {
		List<ExameMaterialAnaliseVO> listaExameMaterialAnaliseVO = new ArrayList<ExameMaterialAnaliseVO>();
		
		try {
			listaExameMaterialAnaliseVO = this.examesService.pesquisarAtivosPorSiglaOuDescricao(descricao);
		} catch (ServiceException e) {
			LOG.error(SERVICE + e.getMessage(), e);
			throw new ApplicationBusinessException(ManterCadastroExamesSignificativosRNExceptionCode.MSG_ERRO_LISTAR_EXAME_MATERIAL);
		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new ApplicationBusinessException(ManterCadastroExamesSignificativosRNExceptionCode.MSG_SERVICO_INDISPONIVEL);
		}
		return listaExameMaterialAnaliseVO;
	}

	public Long pesquisarExameMaterialCount(String descricao) throws ApplicationBusinessException  {
		Long retorno = 0L;
		
		try {
			retorno = this.examesService.pesquisarAtivosPorSiglaOuDescricaoCount(descricao);
		} catch (ServiceException e) {
			LOG.error(SERVICE + e.getMessage(), e);
			throw new ApplicationBusinessException(ManterCadastroExamesSignificativosRNExceptionCode.MSG_ERRO_LISTAR_EXAME_MATERIAL); 
		} catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new ApplicationBusinessException(ManterCadastroExamesSignificativosRNExceptionCode.MSG_SERVICO_INDISPONIVEL); 
		}
		return retorno; 
	}

	public void persitirExameSignificativo(Short unfSeq, String exaSigla, Integer matAnlsSeq, Boolean cargaExame) throws ApplicationBusinessException {
		try {
			examesService.persistirAelUnidExameSignificativo(unfSeq, exaSigla, matAnlsSeq, new Date(), usuario.getMatricula(), usuario.getVinculo(),
					Boolean.FALSE, cargaExame);
		} catch (ServiceException e) {
			LOG.error(SERVICE + e.getMessage(), e);
			throw new ApplicationBusinessException(ManterCadastroExamesSignificativosRNExceptionCode.MSG_ERRO_PERSISTIR_EXAME_SIGNIFICATIVO); 
		}catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new ApplicationBusinessException(ManterCadastroExamesSignificativosRNExceptionCode.MSG_SERVICO_INDISPONIVEL); 
		}
	}

	public void excluirExameSignificativo(Short unfSeq, String exaSigla, Integer matAnlsSeq) throws ApplicationBusinessException {
		try {
			examesService.removerAelUnidExameSignificativo(unfSeq, exaSigla, matAnlsSeq);
		} catch (ServiceException e) {
			LOG.error(SERVICE + e.getMessage(), e);
			throw new ApplicationBusinessException(ManterCadastroExamesSignificativosRNExceptionCode.MSG_ERRO_DELETAR_EXAME_SIGNIFICATIVO); 
		}catch (RuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new ApplicationBusinessException(ManterCadastroExamesSignificativosRNExceptionCode.MSG_SERVICO_INDISPONIVEL); 
		}
	}

	@Override
	protected Log getLogger() {
		return null;
	}

}
