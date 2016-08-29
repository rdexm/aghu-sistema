package br.gov.mec.aghu.perinatologia.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.model.McoProfNascJn;
import br.gov.mec.aghu.model.McoProfNascs;
import br.gov.mec.aghu.model.McoProfNascsId;
import br.gov.mec.aghu.perinatologia.dao.McoProfNascsDAO;
import br.gov.mec.aghu.perinatologia.dao.McoProfNascsJnDAO;
import br.gov.mec.aghu.registrocolaborador.service.IRegistroColaboradorService;
import br.gov.mec.aghu.registrocolaborador.vo.RapServidorConselhoVO;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.service.ServiceException;

@Stateless
public class McoProfNascsRN extends BaseBusiness {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1712613170642413556L;

	@Inject
	@QualificadorUsuario
	private Usuario usuario;

	@Inject
	private IRegistroColaboradorService registroColaboradorService;

	@Inject
	private McoProfNascsDAO mcoProfNascsDAO;

	@Inject
	private McoProfNascsJnDAO mcoProfNascsJnDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Override
	protected Log getLogger() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public enum McoProfNascsRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_SERVICO_INDISPONIVEL, ERRO_EXCLUSAO_PROFISSIONAIS, ERRO_JA_EXISTE_PROFISSIONAL;
	}
	
	/**
	 * RN 02 
	 */
	public List<RapServidorConselhoVO>  listarProfissionais(Short gsoSeqp, Integer gsoPacCodigo, Integer nasSeqp) throws ApplicationBusinessException{
		List<McoProfNascs> listarProfNascs = new LinkedList<McoProfNascs>();
		listarProfNascs = this.mcoProfNascsDAO.listarProfNascs(gsoSeqp, gsoPacCodigo, nasSeqp);
		List<RapServidorConselhoVO> listaRapServidorConselho = new LinkedList<RapServidorConselhoVO>();
		List<String> listaSiglas = obterSiglas();
		for (McoProfNascs mcoProfNascs : listarProfNascs) {
			RapServidorConselhoVO vo = obterServidorConselho(listaSiglas, mcoProfNascs.getId().getSerMatriculaNasc(),mcoProfNascs.getId().getSerVinCodigoNasc());
			if (vo != null ) {
				listaRapServidorConselho.add(vo);
			}

		}
		return listaRapServidorConselho;
		
		
	}
	
	
	public void gravarMcoProfNasc(McoProfNascs profNascs) throws ApplicationBusinessException{
			profNascs = antesGravar(profNascs);
			McoProfNascs existe = this.mcoProfNascsDAO.obterPorChavePrimaria(profNascs.getId());
			if(existe!=null){
				throw new ApplicationBusinessException(McoProfNascsRNExceptionCode.ERRO_JA_EXISTE_PROFISSIONAL);
			}else{
				this.mcoProfNascsDAO.persistir(profNascs);
			}

	}
	
	
	/**
	 * @ORADB MCO_PROF_NASCS.MCOT_PNA_BRI 
	 */
	private McoProfNascs antesGravar(McoProfNascs profNascs){
		profNascs.setCriadoEm(new Date());
		profNascs.setSerMatricula(usuario.getMatricula());
		profNascs.setSerVinCodigo(usuario.getVinculo());
		return profNascs;
	}
	
	
	 /**
	  * Exclui McoProfNasc
	  * @param id
	  * @throws ApplicationBusinessException
	  */
	public void excluirMcoProfNasc(McoProfNascsId id) throws ApplicationBusinessException {
		
		if (excluir(id)) {
			try {
				if (id != null) {
					McoProfNascs origem = this.mcoProfNascsDAO.obterOriginal(id);
					this.mcoProfNascsDAO.removerPorId(origem.getId());
					this.incluirJournal(origem, DominioOperacoesJournal.DEL);
				}
			} catch (RuntimeException e) {
				throw new ApplicationBusinessException(McoProfNascsRNExceptionCode.ERRO_EXCLUSAO_PROFISSIONAIS);
			}
		}
		
	}
	
	/**
	  * Exclui todos profissionais do nascimento
	  * @param id
	  * @throws ApplicationBusinessException
	  */
	public void excluirTodosMcoProfNasc(McoProfNascsId id) throws ApplicationBusinessException{
		if (excluir(id)) {
			List<McoProfNascs> profNascsExclusao = mcoProfNascsDAO.listarProfNascs(id.getNasGsoSeqp(), id.getNasGsoPacCodigo(), id.getNasSeqp());
			for (McoProfNascs mcoProfNascs : profNascsExclusao) {
				try {
					if (mcoProfNascs.getId() != null) {
						McoProfNascs origem = this.mcoProfNascsDAO.obterOriginal(mcoProfNascs);
						this.mcoProfNascsDAO.removerPorId(mcoProfNascs.getId());
						this.incluirJournal(origem, DominioOperacoesJournal.DEL);
					}
				} catch (RuntimeException e) {
					throw new ApplicationBusinessException(McoProfNascsRNExceptionCode.ERRO_EXCLUSAO_PROFISSIONAIS);
				}
			}
		}
	}
	
	private void incluirJournal(McoProfNascs origem, DominioOperacoesJournal operacao){
		McoProfNascJn jn = BaseJournalFactory.getBaseJournal(operacao, McoProfNascJn.class,  usuario.getLogin());
		jn.setCriadoEm(origem.getCriadoEm());
		jn.setNasGsoPacCodigo(origem.getId().getNasGsoPacCodigo());
		jn.setNasGsoSeqp(origem.getId().getNasGsoSeqp());
		jn.setNasSeqp(origem.getId().getNasSeqp());
		jn.setSerMatriculaNasc(origem.getId().getSerMatriculaNasc());
		jn.setSerVinCodigoNasc(origem.getId().getSerVinCodigoNasc());
		
		jn.setSerMatricula(origem.getSerMatricula());
		jn.setSerVinCodigo(origem.getSerVinCodigo());
		this.mcoProfNascsJnDAO.persistir(jn);
	}
	
	
	
	/**
	 * Verifica se deve excluir um McoProfNasc
	 * @param id
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private boolean excluir(McoProfNascsId id) {
		if (!this.mcoProfNascsDAO.existeProfNasc(id.getNasGsoSeqp(), id.getNasGsoPacCodigo(), id.getNasSeqp())) {
			return false;
		}
		return true;
	}
	private RapServidorConselhoVO obterServidorConselho(List<String> listaSiglas, Integer matricula, Short vinculo) {
		RapServidorConselhoVO vo = null;
		vo = this.registroColaboradorService.obterServidorConselhoPorSiglaMatriculaVinculo(listaSiglas, matricula, vinculo);
		return vo;
	}

	public List<RapServidorConselhoVO> pesquisarServidoresConselhoPorSiglaCentroCusto(final String sigla) throws ApplicationBusinessException{
		List<RapServidorConselhoVO> listaVO = null;
		try {
			List<String> listaSiglas = new LinkedList<String>();
			List<Integer> listaCentroCusto = new LinkedList<Integer>();
			listaSiglas.add(sigla);
			listaCentroCusto = obterCentroCustos();
			listaVO = this.registroColaboradorService.pesquisarServidoresConselhoPorSiglaCentroCusto(listaSiglas, listaCentroCusto);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(McoProfNascsRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		

		return listaVO;
	}
	
	public Long pesquisarServidoresConselhoPorSiglaCentroCustoCount(final String sigla) throws ApplicationBusinessException{
		try {
			List<String> listaSiglas = new LinkedList<String>();
			List<Integer> listaCentroCusto = new LinkedList<Integer>();
			listaSiglas.add(sigla);
			listaCentroCusto = obterCentroCustos();
			return this.registroColaboradorService.pesquisarServidoresConselhoPorSiglaCentroCustoCount(listaSiglas, listaCentroCusto);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(McoProfNascsRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
	}
	
	private List<Integer> obterCentroCustos() throws ApplicationBusinessException{
		String param;
		try {
			param = buscarAghParametro("P_CENTRO_CUSTO_PERINATOLOGIA", "vlrTexto");
			if (param != null) {
				String[] centroCustos = param.split(",");
				List<Integer> listaCentroCustos = new ArrayList<Integer>();
				for (String centroCusto : centroCustos) {
					if (StringUtils.isNotBlank(centroCusto)) {
						if (CoreUtil.isNumeroInteger(StringUtils.strip(centroCusto))) {
							listaCentroCustos.add(Integer.valueOf(StringUtils.strip(centroCusto)));
						}
						
					}
				}
				return listaCentroCustos;
			}
		} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("Parâmetro obrigatório P_CENTRO_CUSTO_PERINATOLOGIA  não existe no banco");
		}
		return null;
		
	}
	
	
	
	private List<String> obterSiglas() throws ApplicationBusinessException{
		String param;
		try {
			param = buscarAghParametro("P_AGHU_CONSELHOS_PERINATOLOGIA_NASCIMENTO", "vlrTexto");
			if (param != null) {
				String[] siglas = param.split(",");
				List<String> listaSiglas = new ArrayList<String>();
				for (String sigla : siglas) {
					if (StringUtils.isNotBlank(sigla)) {
						listaSiglas.add(StringUtils.strip(sigla));
						}
						
					}
				return listaSiglas;
			}
		} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("Parâmetro obrigatório P_AGHU_CONSELHOS_PERINATOLOGIA_NASCIMENTO  não existe no banco");
		}
		return null;
		
	}

	
	private String buscarAghParametro(final String param, final String valor) throws ApplicationBusinessException{
		try {
			Object parametro = parametroFacade.obterAghParametroPorNome(param, valor);		
			String retorno = ((String) parametro).toString();
			if (retorno == null || retorno.isEmpty()){
				 throw new ApplicationBusinessException(McoProfNascsRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
			}else{
				return retorno;
				}		
		} catch (RuntimeException e) {
			throw new IllegalArgumentException("Problema ao obter o parametro" + param);
		}
	}


	public List<RapServidorConselhoVO> pesquisarServidoresPorSiglaConselhoNumeroNome(String sigla, String param) throws ApplicationBusinessException{
		List<RapServidorConselhoVO> listaVOFiltrada = new ArrayList<RapServidorConselhoVO>();
		try {
			List<String> listaSiglas = new LinkedList<String>();
			List<Integer> listaCentroCusto = new LinkedList<Integer>();
			
			if(sigla == null){
				listaSiglas.addAll(obterSiglas());
			}else{
				listaSiglas.add(sigla);
			}
			
			listaCentroCusto = obterCentroCustos();
			List<RapServidorConselhoVO> listaVO = this.registroColaboradorService.pesquisarServidoresConselhoPorSiglaCentroCusto(listaSiglas, listaCentroCusto);
			
			if(param == null || param.isEmpty()){
				return listaVO;
			}else{
				for(RapServidorConselhoVO servidor : listaVO){
					if(servidor.getNroRegConselho() != null && servidor.getNroRegConselho().equals(param)){
						listaVOFiltrada.add(servidor);
						return listaVOFiltrada;
					}
				}
				if(listaVOFiltrada.isEmpty()){
					for(RapServidorConselhoVO servidor : listaVO){
						if(servidor.getNome().toUpperCase().contains(param.toUpperCase())){
							listaVOFiltrada.add(servidor);
						}
					}
				}
			}
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(McoProfNascsRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}		

		return listaVOFiltrada;
	}
	
	public Integer pesquisarServidoresPorSiglaConselhoNumeroNomeCount(String sigla, String param) throws ApplicationBusinessException{
		List<RapServidorConselhoVO> listaVOFiltrada = new ArrayList<RapServidorConselhoVO>();
		try {
			List<String> listaSiglas = new LinkedList<String>();
			List<Integer> listaCentroCusto = new LinkedList<Integer>();
			
			if(sigla == null){
				listaSiglas.addAll(obterSiglas());
			}else{
				listaSiglas.add(sigla);
			}
			
			listaCentroCusto = obterCentroCustos();
			
			List<RapServidorConselhoVO> listaVO = this.registroColaboradorService.pesquisarServidoresConselhoPorSiglaCentroCusto(listaSiglas, listaCentroCusto, null);
						
			if(param == null || param.isEmpty()){
				return listaVO.size();
			}else{
				for(RapServidorConselhoVO servidor : listaVO){
					if(servidor.getNroRegConselho() != null && servidor.getNroRegConselho().equals(param)){
						listaVOFiltrada.add(servidor);
						break;
					}
				}
				if(listaVOFiltrada.isEmpty()){
					for(RapServidorConselhoVO servidor : listaVO){
						if(servidor.getNome().toUpperCase().contains(param.toUpperCase())){
							listaVOFiltrada.add(servidor);
						}
					}
				}
			}
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(McoProfNascsRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}		

		return listaVOFiltrada.size();
	}
}
