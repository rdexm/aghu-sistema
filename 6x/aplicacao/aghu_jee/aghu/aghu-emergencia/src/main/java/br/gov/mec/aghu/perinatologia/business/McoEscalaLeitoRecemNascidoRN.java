package br.gov.mec.aghu.perinatologia.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.internacao.service.IInternacaoService;
import br.gov.mec.aghu.internacao.vo.LeitoVO;
import br.gov.mec.aghu.model.McoEscalaLeitoRecemNascido;
import br.gov.mec.aghu.model.McoEscalaLeitoRecemNascidoId;
import br.gov.mec.aghu.model.McoEscalaLeitoRecemNascidoJn;
import br.gov.mec.aghu.perinatologia.dao.McoEscalaLeitoRecemNascidoDAO;
import br.gov.mec.aghu.perinatologia.dao.McoEscalaLeitoRecemNascidoJnDAO;
import br.gov.mec.aghu.perinatologia.vo.EscalaLeitoRecemNascidoVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.service.IRegistroColaboradorService;
import br.gov.mec.aghu.registrocolaborador.vo.Servidor;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.util.EmergenciaParametrosColunas;
import br.gov.mec.aghu.util.EmergenciaParametrosEnum;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.service.ServiceException;

@Stateless
public class McoEscalaLeitoRecemNascidoRN extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -544790298518279918L;

	@Inject
	private McoEscalaLeitoRecemNascidoDAO mcoEscalaLeitoRecemNascidoDAO;
	
	@Inject
	private McoEscalaLeitoRecemNascidoJnDAO mcoEscalaLeitoRecemNascidoJnDAO;

	@Inject
	private IRegistroColaboradorService registroColaboradorService;
	
	@Inject
	private IInternacaoService internacaoService;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	@QualificadorUsuario
	private Usuario usuario;

	@Override
	@Deprecated
	protected Log getLogger() {
		return null;
	}

	private enum McoEscalaLeitoRecemNascidoRNException implements
			BusinessExceptionCode {
		MENSAGEM_ERRO_SERVICO_EL, MCO_00656, REGISTRO_INEXISTENTE_EL, LEITO_INEXISTENTE_EL, SERVIDOR_INEXISTENTE_EL;
	}

	//C1
	public List<EscalaLeitoRecemNascidoVO> pesquisarEscalaLeitoRecemNascido(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Servidor servidor, LeitoVO leito)
			throws ApplicationBusinessException {
		
		Short vinculo = null;
		Integer matricula = null;
		if(servidor != null) {
			vinculo = servidor.getVinculo();
			matricula = servidor.getMatricula();
		}
		List<EscalaLeitoRecemNascidoVO> resultado = new ArrayList<EscalaLeitoRecemNascidoVO>();
		List<McoEscalaLeitoRecemNascido> lista = mcoEscalaLeitoRecemNascidoDAO
					.pesquisarMcoEscalaLeitoRecemNascido(verificarFiltroLeitoID(leito), vinculo, matricula);
			if (isNotListaVazia(lista)) {
				prepararItens(resultado, lista);
				resultado = CoreUtil.paginarListaResultadosMemoria(firstResult, maxResults, resultado);
				CoreUtil.ordenarLista(resultado, EscalaLeitoRecemNascidoVO.Fields.LEITO.toString(), asc);
			}
			return resultado;
		}

	private String verificarFiltroLeitoID(LeitoVO leito) {
		String leitoID = leito == null? "" : leito.getLeitoID();
		return leitoID;
	}
	
	// C2 #42540
	public List<LeitoVO> pesquisarLeitoPorLeitoIDUnfs(String param) throws ApplicationBusinessException {
		try {
			return internacaoService.pesquisarLeitosPorSeqUnf(buscarUnfs(), param);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(
					McoEscalaLeitoRecemNascidoRNException.MENSAGEM_ERRO_SERVICO_EL);
		} 
	}
	
	//C3
	public List<Servidor> buscarProfissionaisNeoPediatriaPorCodigoMatriculaNome(
			String param) throws ApplicationBusinessException {
		if (CoreUtil.isNumeroInteger(param)) {
			return pesquisarVRapPessoaServidorPorVinCodigoMatriculaCCTNome(buscarVinCodigos(), buscarCodigosCentroCusto(), Integer.valueOf(param), null);
		} else {
			List<Short> vinCodigos = buscarVinCodigos();
			List<Integer> ccLista = buscarCodigosCentroCusto();

			return pesquisarVRapPessoaServidorPorVinCodigoMatriculaCCTNome(vinCodigos, ccLista, null, param);
		}
	}

	//C4
	private void existeMcoEscalaLeitoRecemNascidoCadastrado(String leito, Short vinculo, Integer matricula) throws ApplicationBusinessException {
		List<McoEscalaLeitoRecemNascido> lista =  mcoEscalaLeitoRecemNascidoDAO.pesquisarMcoEscalaLeitoRecemNascido(leito, vinculo, matricula);
		if(isNotListaVazia(lista)) {
			throw new ApplicationBusinessException(McoEscalaLeitoRecemNascidoRNException.MCO_00656);
		}
	}
	
	private List<Short> buscarVinCodigos() throws ApplicationBusinessException {
		BigDecimal codFunc = (BigDecimal) buscarParametro("P_AGHU_VINC_FUNC" , EmergenciaParametrosColunas.VLR_NUMERICO.toString());
		BigDecimal codProf = (BigDecimal) buscarParametro("P_COD_VINCULO_PROFESSOR", EmergenciaParametrosColunas.VLR_NUMERICO.toString());
		ArrayList<Short> codigos = new ArrayList<>();
		codigos.add(codFunc  == null ? null : codFunc.shortValue());
		codigos.add(codProf  == null ? null : codProf.shortValue());
		return codigos;
	}

	private List<Short> buscarUnfs() throws ApplicationBusinessException {
		BigDecimal unidadeObs = (BigDecimal) buscarParametro(EmergenciaParametrosEnum.P_UNIDADE_INT_OBS.toString(), EmergenciaParametrosColunas.VLR_NUMERICO.toString());
		BigDecimal unidadeAloj = (BigDecimal) buscarParametro(EmergenciaParametrosEnum.P_UNIDADE_ALOJ_CJTO.toString(), EmergenciaParametrosColunas.VLR_NUMERICO.toString());
		ArrayList<Short> unfs = new ArrayList<>();
		unfs.add(unidadeObs == null ? null: unidadeObs.shortValue());
		unfs.add(unidadeAloj == null ? null: unidadeAloj.shortValue());
		return unfs;
	}
	
	private List<Integer> buscarCodigosCentroCusto() throws ApplicationBusinessException {
		BigDecimal neoCC = (BigDecimal) buscarParametro(EmergenciaParametrosEnum.P_CCUSTO_NEONATOLOGIA.toString(), EmergenciaParametrosColunas.VLR_NUMERICO.toString());
		BigDecimal pedCC = (BigDecimal) buscarParametro(EmergenciaParametrosEnum.P_CCUSTO_PEDIATRIA.toString(), EmergenciaParametrosColunas.VLR_NUMERICO.toString());
		ArrayList<Integer> ccs = new ArrayList<>();
		ccs.add(neoCC == null? null : neoCC.intValue());
		ccs.add(pedCC == null? null : pedCC.intValue());
		return ccs;
	}
	
	private Object buscarParametro(String nome, String tipo) throws ApplicationBusinessException {
		return parametroFacade.obterAghParametroPorNome(nome, tipo);
	}

	private void prepararItens(
			List<EscalaLeitoRecemNascidoVO> resultado,
			List<McoEscalaLeitoRecemNascido> lista)
			throws ApplicationBusinessException {
		Servidor vo;
		String nome = "";
		String nomeUsual = "";
		for (McoEscalaLeitoRecemNascido item : lista) {
			vo = obterServidorVOPorId(item.getServidorVinCodigoResponsavel(), item.getServidorMatriculaResponsavel());
				if(vo != null) {
					nome = vo.getNomePessoaFisica();
					nomeUsual = vo.getNomeUsual();
				}
				resultado.add(new EscalaLeitoRecemNascidoVO(item.getLeitoID(), item.getServidorVinCodigoResponsavel(), item.getServidorMatriculaResponsavel(),
						nome, nomeUsual));
		}
	}


	private Servidor obterServidorVOPorId(Short vinculo, Integer matricula)
			throws ApplicationBusinessException {
		try {
			return registroColaboradorService
					.obterVRapPessoaServidorPorVinCodigoMatricula(matricula,
							vinculo);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(
					McoEscalaLeitoRecemNascidoRNException.MENSAGEM_ERRO_SERVICO_EL);
		}
	}
	
	@SuppressWarnings("rawtypes")
	private boolean isNotListaVazia(List lista) {
		return lista != null && !lista.isEmpty();
	}

	//C5
	private boolean existeLeito(String leitoID) throws ApplicationBusinessException {
		try {
			return internacaoService.verificarLeitoExiste(leitoID);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(McoEscalaLeitoRecemNascidoRNException.MENSAGEM_ERRO_SERVICO_EL);
		}
	}
	
	// C6
	private boolean existeServidor(Short codigo, Integer matricula ) throws ApplicationBusinessException {
		try {
			return registroColaboradorService.verificarServidorExiste(codigo, matricula);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(McoEscalaLeitoRecemNascidoRNException.MENSAGEM_ERRO_SERVICO_EL);
		}
	}
	
	public void inserirMcoEscalaLeitoRecemNascido(McoEscalaLeitoRecemNascido entity) throws ApplicationBusinessException {
		validarInsercao(entity);
		entity.setCriadoEm(new Date());
		entity.setServidorMatricula(usuario.getMatricula());
		entity.setServidorVinCodigo(usuario.getVinculo());
		mcoEscalaLeitoRecemNascidoDAO.persistir(entity);
	}

	private void validarInsercao(McoEscalaLeitoRecemNascido entity)
			throws ApplicationBusinessException {
		if(!existeLeito(entity.getLeitoID())) {
			throw new ApplicationBusinessException(McoEscalaLeitoRecemNascidoRNException.LEITO_INEXISTENTE_EL);
		}
		if(!existeServidor(entity.getServidorVinCodigoResponsavel(), entity.getServidorMatriculaResponsavel())){
			throw new ApplicationBusinessException(McoEscalaLeitoRecemNascidoRNException.SERVIDOR_INEXISTENTE_EL);
		}
		existeMcoEscalaLeitoRecemNascidoCadastrado(entity.getLeitoID(), entity.getServidorVinCodigoResponsavel(), entity.getServidorMatriculaResponsavel());
	}
	
	public void deletarMcoEscalaLeitoRecemNascidoPorId(McoEscalaLeitoRecemNascidoId id) throws BaseException {
		McoEscalaLeitoRecemNascido entidade = mcoEscalaLeitoRecemNascidoDAO.obterPorChavePrimaria(id);
		if(entidade == null) {
			throw new BaseException(McoEscalaLeitoRecemNascidoRNException.REGISTRO_INEXISTENTE_EL);
		}
		persistirJournal(entidade);
		mcoEscalaLeitoRecemNascidoDAO.remover(entidade);
	}

	private void persistirJournal(McoEscalaLeitoRecemNascido entidade) {
		McoEscalaLeitoRecemNascidoJn journal = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.DEL, McoEscalaLeitoRecemNascidoJn.class, usuario.getLogin());
		journal.setCriadoEm(new Date());
		journal.setLtoLtoId(entidade.getLeitoID());
		journal.setSerMatriculaResp(entidade.getServidorMatriculaResponsavel());
		journal.setSerVinCodigoResp(entidade.getServidorVinCodigoResponsavel());
		mcoEscalaLeitoRecemNascidoJnDAO.persistir(journal);
	}

	public Long pesquisarEscalaLeitoRecemNascidoCount(Servidor servidor, LeitoVO leito) throws ApplicationBusinessException {
			Short vinculo = null;
			Integer matricula = null;
			if(servidor != null) {
				vinculo = servidor.getVinculo();
				matricula = servidor.getMatricula();
			}
			List<EscalaLeitoRecemNascidoVO> itens = new ArrayList<EscalaLeitoRecemNascidoVO>();
			List<McoEscalaLeitoRecemNascido> lista = mcoEscalaLeitoRecemNascidoDAO
					.pesquisarMcoEscalaLeitoRecemNascido(verificarFiltroLeitoID(leito),
							vinculo, matricula);
				prepararItens(itens, lista);
			if(itens != null && !itens.isEmpty()) {
				return ((Integer)(itens.size())).longValue();
			}
		return 0L;
	}

	public Long pesquisarLeitoPorLeitoIDUnfsCount(String param)  throws ApplicationBusinessException  {
		try {
			return internacaoService.pesquisarLeitosPorSeqUnfCount(buscarUnfs(), param);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(McoEscalaLeitoRecemNascidoRNException.MENSAGEM_ERRO_SERVICO_EL);
		}
	}

	public Long buscarProfissionaisNeoPediatriaPorCodigoMatriculaNomeCount(String param) throws ApplicationBusinessException {
		if (CoreUtil.isNumeroInteger(param)) {
			return registroColaboradorFacade.pesquisarVRapPessoaServidorPorVinCodigoMatriculaCCTNomeCount(
						buscarVinCodigos(), buscarCodigosCentroCusto(),
						Integer.valueOf(param), null);
		} else {
			return registroColaboradorFacade.pesquisarVRapPessoaServidorPorVinCodigoMatriculaCCTNomeCount(
					buscarVinCodigos(), buscarCodigosCentroCusto(), null, param);
		}		
	}
	
	public List<Servidor> pesquisarVRapPessoaServidorPorVinCodigoMatriculaCCTNome(
			List<Short> vinCodigos, List<Integer> cctCodigos,
			Integer matriculaVinCodigo, String nome) {
		List<Object[]> lista = null;
		List<Servidor> resultado = new ArrayList<Servidor>();
		lista = this.registroColaboradorFacade.pesquisarVRapPessoaServidorPorVinCodigoMatriculaCCTNome(vinCodigos, cctCodigos, matriculaVinCodigo, nome);
		
		if (lista == null || lista.isEmpty()) {
			return resultado;
		}

		for (Object[] item : lista) {
			Servidor servidor = new Servidor();
			servidor.setVinculo((Short) item[0]);
			servidor.setMatricula((Integer) item[1]);
			servidor.setNomePessoaFisica((String) item[2]);
			servidor.setNomeUsual((String) item[3]);
			servidor.setIndSituacao((String) item[4]);
			resultado.add(servidor);
		}

		return resultado;
	}
}
