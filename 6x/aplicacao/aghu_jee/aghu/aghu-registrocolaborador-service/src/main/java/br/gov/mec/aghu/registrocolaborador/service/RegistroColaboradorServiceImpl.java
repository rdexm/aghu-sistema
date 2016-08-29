package br.gov.mec.aghu.registrocolaborador.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VRapServidorConselho;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.vo.InstQualificadora;
import br.gov.mec.aghu.registrocolaborador.vo.InstQualificadoraFiltro;
import br.gov.mec.aghu.registrocolaborador.vo.RapPessoasFisicaVO;
import br.gov.mec.aghu.registrocolaborador.vo.RapServidorConselhoVO;
import br.gov.mec.aghu.registrocolaborador.vo.Servidor;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.registrocolaborador.vo.UsuarioFiltro;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.messages.MessagesUtils;
import br.gov.mec.aghu.service.ServiceBusinessException;
import br.gov.mec.aghu.service.ServiceException;

@Stateless
@Deprecated
public class RegistroColaboradorServiceImpl implements
		IRegistroColaboradorService {

    @EJB
    private IRegistroColaboradorFacade registroColaboradorFacade;
    
    @Inject
    private MessagesUtils messagesUtils;
    
	@Override
	public Usuario buscaUsuario(UsuarioFiltro filtro)
			throws ServiceException {		
    	
		Usuario usuario = new Usuario();
    	try {
    		RapServidores rapServidor = this.registroColaboradorFacade.obterServidorAtivoPorUsuario(filtro.getLogin());    		
        	usuario.setVinculo(rapServidor.getId().getVinCodigo());
        	usuario.setMatricula(rapServidor.getId().getMatricula());
        	usuario.setLogin(rapServidor.getUsuario());
        	
		} catch (ApplicationBusinessException e) {
			throw new ServiceException();
		}    	
    	
    	return usuario;
		
	}

	@Override
	public List<InstQualificadora> buscaInstQualificadora(
			InstQualificadoraFiltro instQualificadoraFiltro)
			throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Servidor> pesquisarMedicosEmergencia(List<Short> vinCodigos, List<Integer> matriculas, String nome,
			Integer firstResult, Integer maxResults, String orderProperty, boolean asc) throws ServiceException {
		List<RapServidores> rapServidores = this.registroColaboradorFacade.pesquisarMedicosEmergencia(vinCodigos, matriculas, nome, firstResult, maxResults, orderProperty, asc);
		List<Servidor> servidores = new ArrayList<Servidor>();
		for (RapServidores rapServidor : rapServidores) {
			Servidor servidor = new Servidor();
			servidor.setVinculo(rapServidor.getId().getVinCodigo());
			servidor.setMatricula(rapServidor.getId().getMatricula());
//			servidor.setIndSituacao(rapServidor.getIndSituacao() != null ? rapServidor.getIndSituacao().name() : null);
			servidor.setNomePessoaFisica(rapServidor.getPessoaFisica().getNome());
//			servidor.setCodigoPessoaFisica(rapServidor.getPessoaFisica().getCodigo());
			servidores.add(servidor);
		}
		return servidores;
	}
	
	@Override
	public List<Servidor> pesquisarMedicosEmergencia(List<Short> vinCodigos, List<Integer> matriculas, String nome) throws ServiceException {
		List<RapServidores> rapServidores = this.registroColaboradorFacade.pesquisarMedicosEmergencia(vinCodigos, matriculas, nome);
		List<Servidor> servidores = new ArrayList<Servidor>();
		for (RapServidores rapServidor : rapServidores) {
			Servidor servidor = new Servidor();
			servidor.setVinculo(rapServidor.getId().getVinCodigo());
			servidor.setMatricula(rapServidor.getId().getMatricula());
//			servidor.setIndSituacao(rapServidor.getIndSituacao() != null ? rapServidor.getIndSituacao().name() : null);
			servidor.setNomePessoaFisica(rapServidor.getPessoaFisica().getNome());
//			servidor.setCodigoPessoaFisica(rapServidor.getPessoaFisica().getCodigo());
			servidores.add(servidor);
		}
		return servidores;
	}
	
	@Override
	public Long pesquisarMedicosEmergenciaCount(List<Short> vinCodigos, List<Integer> matriculas, String nome) throws ServiceException {
		return this.registroColaboradorFacade.pesquisarMedicosEmergenciaCount(vinCodigos, matriculas, nome);
	}

	@Override
	public List<Servidor> pesquisarServidoresAtivos(Short vinCodigo, Integer matricula, String nome, Integer maxResults) throws ServiceException {
		List<RapServidores> rapServidores = this.registroColaboradorFacade.pesquisarServidoresAtivos(vinCodigo, matricula, nome, maxResults);
		List<Servidor> servidores = new ArrayList<Servidor>();
		for (RapServidores rapServidor : rapServidores) {
			Servidor servidor = new Servidor();
			servidor.setVinculo(rapServidor.getId().getVinCodigo());
			servidor.setMatricula(rapServidor.getId().getMatricula());
			servidor.setIndSituacao(rapServidor.getIndSituacao() != null ? rapServidor.getIndSituacao().name() : null);
			servidor.setNomePessoaFisica(rapServidor.getPessoaFisica().getNome());
			//servidor.setCodigoPessoaFisica(rapServidor.getPessoaFisica().getCodigo());
			servidores.add(servidor);
		}
		return servidores;
	}
	
	@Override
	public List<Servidor> pesquisarServidoresAtivos(Short vinCodigo, Integer matricula, String nome) throws ServiceException {
		return this.pesquisarServidoresAtivos(vinCodigo, matricula, nome, null);
	}
	
	@Override
	public Long pesquisarServidoresAtivosCount(Short vinCodigo, Integer matricula, String nome) throws ServiceException {
		return this.registroColaboradorFacade.pesquisarServidoresAtivosCount(vinCodigo, matricula, nome);
	}
	
	
	/**
	 * #36698 - Pesquisa profissional pela central de custo
	 * @param strPesquisa
	 * @param centroCusto
	 * @return
	 */
	@Override
	public List<RapServidorConselhoVO> pesquisarServidoresConselho(String strPesquisa, List<Integer> centroCusto) throws ServiceException {
		List<VRapServidorConselho> servidores = this.registroColaboradorFacade.pesquisarServidoresConselho(strPesquisa, centroCusto);
		List<RapServidorConselhoVO> listaVO = new ArrayList<RapServidorConselhoVO>();
		for (VRapServidorConselho vRapServidorConselho : servidores) {
			RapServidorConselhoVO vo = new RapServidorConselhoVO();
			vo.setSigla(vRapServidorConselho.getSigla());
			vo.setNroRegConselho(vRapServidorConselho.getNroRegConselho());
			vo.setSituacao(vRapServidorConselho.getIndSituacao());
			vo.setDtInicioVinculo(vRapServidorConselho.getDtInicioVinculo());
			vo.setDtFimVinculo(vRapServidorConselho.getDtFimVinculo());
			vo.setNome(vRapServidorConselho.getNome());
			vo.setVinCodigo(vRapServidorConselho.getId().getVinCodigo());
			vo.setMatricula(vRapServidorConselho.getId().getMatricula());
			listaVO.add(vo);
		}
		return listaVO;
	}
	
	/**
	 * #36698 - Count Pesquisa profissional pela central de custo
	 * @param strPesquisa
	 * @param centroCusto
	 * @return
	 */
	@Override
	public Long pesquisarServidoresConselhoCount(String strPesquisa,List<Integer> centroCusto) throws ServiceException	{
		return this.registroColaboradorFacade.pesquisarServidoresConselhoCount(strPesquisa, centroCusto);
	}

	@Override
	public List<RapServidorConselhoVO> pesquisarServidoresConselhoPorSiglaCentroCusto(List<String> siglas, List<Integer> centroCusto) throws ServiceException {
		return pesquisarServidoresConselhoPorSiglaCentroCusto(siglas, centroCusto, 100);
	}
	
	@Override
	public List<RapServidorConselhoVO> pesquisarServidoresConselhoPorSiglaCentroCusto(List<String> siglas, List<Integer> centroCusto, Integer maxResults) throws ServiceException {
		List<VRapServidorConselho> servidores = this.registroColaboradorFacade.pesquisarServidoresConselhoPorSiglaCentroCusto(siglas, centroCusto, maxResults);
		List<RapServidorConselhoVO> listaVO = new ArrayList<RapServidorConselhoVO>();
		for (VRapServidorConselho vRapServidorConselho : servidores) {
			RapServidorConselhoVO vo = new RapServidorConselhoVO();
			vo.setNroRegConselho(vRapServidorConselho.getNroRegConselho());
			vo.setNome(vRapServidorConselho.getNome());
			vo.setSigla(vRapServidorConselho.getSigla());
			vo.setSituacao(vRapServidorConselho.getIndSituacao());
			vo.setDtInicioVinculo(vRapServidorConselho.getDtInicioVinculo());
			vo.setDtFimVinculo(vRapServidorConselho.getDtFimVinculo());
			vo.setVinCodigo(vRapServidorConselho.getId().getVinCodigo());
			vo.setMatricula(vRapServidorConselho.getId().getMatricula());
			listaVO.add(vo);
		}
		return listaVO;
	}



	@Override
	public Long pesquisarServidoresConselhoPorSiglaCentroCustoCount(List<String> siglas, List<Integer> centroCusto) throws ServiceException {
		return this.registroColaboradorFacade.pesquisarServidoresConselhoPorSiglaCentroCustoCount(siglas, centroCusto);
	}

	@Override
	public RapServidorConselhoVO obterServidorConselhoPorSiglaMatriculaVinculo(List<String> siglas, Integer matricula, Short vinculo) {
		VRapServidorConselho vRapServidorConselho = this.registroColaboradorFacade.obterServidorConselhoPorSiglaMatriculaVinculo(siglas, matricula, vinculo);
		RapServidorConselhoVO rapServidorConselhoVO = null;
		if (vRapServidorConselho != null) {
			rapServidorConselhoVO = new RapServidorConselhoVO();
			rapServidorConselhoVO.setNroRegConselho(vRapServidorConselho.getNroRegConselho());
			rapServidorConselhoVO.setNome(vRapServidorConselho.getNome());
			rapServidorConselhoVO.setSigla(vRapServidorConselho.getSigla());
			rapServidorConselhoVO.setSituacao(vRapServidorConselho.getIndSituacao());
			rapServidorConselhoVO.setDtInicioVinculo(vRapServidorConselho.getDtInicioVinculo());
			rapServidorConselhoVO.setDtFimVinculo(vRapServidorConselho.getDtFimVinculo());
			rapServidorConselhoVO.setVinCodigo(vRapServidorConselho.getId().getVinCodigo());
			rapServidorConselhoVO.setMatricula(vRapServidorConselho.getId().getMatricula());
		}
		return rapServidorConselhoVO;
	}
	
	
	/**
	 * #36699 - Serviço para obter pessoa fisica
	 * @param vinculos
	 * @param matriculas
	 * @return
	 * @throws ServiceException 
	 */
	@Override
	public List<RapPessoasFisicaVO> obterRapPessoasFisicasPorMatriculaVinculo(List<Short> vinculos, List<Integer> matriculas) throws ServiceException{
		if (vinculos == null && matriculas == null ) {
			throw new ServiceException("Parametros vinculos e matriculas estão vazios");
		}
		List<RapPessoasFisicas> listaRapPessoasFisicas = this.registroColaboradorFacade.obterRapPessoasFisicasPorMatriculaVinculo(vinculos, matriculas);
		List<RapPessoasFisicaVO> listaVO = new LinkedList<RapPessoasFisicaVO>(); 
		for (RapPessoasFisicas rapPessoasFisicas : listaRapPessoasFisicas) {
			 RapPessoasFisicaVO vo = new RapPessoasFisicaVO();
			 vo.setNome(rapPessoasFisicas.getNome());
			 vo.setMatricula(rapPessoasFisicas.getRapServidores().getId().getMatricula());
			 vo.setVinCodigo(rapPessoasFisicas.getRapServidores().getId().getVinCodigo());
			 vo.setSituacao(rapPessoasFisicas.getRapServidores().getIndSituacao().getDescricao());
			 listaVO.add(vo);
		}
		return listaVO;
	}
	
	public String buscarNomeResponsavelPorMatricula(Short codigo, Integer matricula) throws ServiceException {
		return this.registroColaboradorFacade.buscarNomeResponsavelPorMatricula(codigo, matricula);
	}


	@Override
	public Servidor buscarServidor(String username) throws ServiceBusinessException, ServiceException {
		Servidor result = null;
		try {
			RapServidores rapServidor = registroColaboradorFacade.obterServidorAtivoPorUsuario(username);
			if (rapServidor != null) {
				result = new Servidor();
				result.setVinculo(rapServidor.getId().getVinCodigo());
				result.setMatricula(rapServidor.getId().getMatricula());
			}
			return result;
		} catch (ApplicationBusinessException e) {
			throw new ServiceBusinessException(messagesUtils.getResourceBundleValue(e));
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	public boolean usuarioTemPermissaoParaImprimirRelatoriosDefinitivos(Integer matricula, Short vinCodigo, String[] siglas) throws ServiceException {
		try {
			return this.registroColaboradorFacade.usuarioTemPermissaoParaImprimirRelatoriosDefinitivos(matricula, vinCodigo, siglas);
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}	
	}
	
	@Override
	public RapServidorConselhoVO usuarioRelatoriosDefinitivos(Integer matricula, Short vinCodigo, String[] siglas) throws ServiceException {
		try {
			VRapServidorConselho vRapServidorConselho = this.registroColaboradorFacade.usuarioRelatoriosDefinitivos(matricula, vinCodigo, siglas);
			RapServidorConselhoVO rapServidorConselhoVO = null;
			if (vRapServidorConselho != null) {
				rapServidorConselhoVO = new RapServidorConselhoVO();
				rapServidorConselhoVO.setNroRegConselho(vRapServidorConselho.getNroRegConselho());
				rapServidorConselhoVO.setNome(vRapServidorConselho.getNome());
				rapServidorConselhoVO.setSigla(vRapServidorConselho.getSigla());
				rapServidorConselhoVO.setSituacao(vRapServidorConselho.getIndSituacao());
				rapServidorConselhoVO.setDtInicioVinculo(vRapServidorConselho.getDtInicioVinculo());
				rapServidorConselhoVO.setDtFimVinculo(vRapServidorConselho.getDtFimVinculo());
				rapServidorConselhoVO.setVinCodigo(vRapServidorConselho.getId().getVinCodigo());
				rapServidorConselhoVO.setMatricula(vRapServidorConselho.getId().getMatricula());
			}
			return rapServidorConselhoVO; 
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}	
	}

	@Override
	public Boolean verificarServidorExiste(Short codigo, Integer matricula) throws ServiceException {	
		try {
			return this.registroColaboradorFacade.verificarServidorExiste(codigo, matricula);
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public Servidor obterVRapPessoaServidorPorVinCodigoMatricula(Integer matricula, Short vinCodigo) throws ServiceException {
		try {
			Servidor servidor = new Servidor();
			Object[] item = this.registroColaboradorFacade.obterVRapPessoaServidorPorVinCodigoMatricula(matricula, vinCodigo); 
			if(item == null) {
				return null;
			}
			servidor.setVinculo((Short)item[0]);
			servidor.setMatricula((Integer)item[1]);
			servidor.setNomePessoaFisica((String)item[2]);
			servidor.setNomeUsual((String)item[3]);
			servidor.setIndSituacao((String)item[4]);
			 return servidor;
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public List<Servidor> pesquisarVRapPessoaServidorPorVinCodigoMatriculaCCTNome(
			List<Short> vinCodigos, List<Integer> cctCodigos, Integer matriculaVinCodigo, String nome)
			throws ServiceException {
		List<Object[]>  lista = null;
		List<Servidor> resultado = new ArrayList<Servidor>();
		try {
			lista = this.registroColaboradorFacade.pesquisarVRapPessoaServidorPorVinCodigoMatriculaCCTNome(
					vinCodigos,cctCodigos,matriculaVinCodigo,nome);
			if(lista == null || lista.isEmpty()) {
				return resultado;
			}
			
			for(Object[] item : lista) {
				Servidor servidor = new Servidor();
				 servidor.setVinculo((Short)item[0]);
				 servidor.setMatricula((Integer) item[1]);
				 servidor.setNomePessoaFisica((String) item[2]);
				 servidor.setNomeUsual((String) item[3]);
				 servidor.setIndSituacao((String)item[4]);
				 resultado.add(servidor);
			}
			
			 return resultado;
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}	
	}

	@Override
	public Long pesquisarVRapPessoaServidorPorVinCodigoMatriculaCCTNomeCount(
			List<Short> vinCodigos, List<Integer> cctCodigos, Integer matriculaVinCodigo, String nome) throws ServiceException {
		try {
			return this.registroColaboradorFacade.pesquisarVRapPessoaServidorPorVinCodigoMatriculaCCTNomeCount(vinCodigos,cctCodigos,matriculaVinCodigo,nome);
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public RapServidorConselhoVO obterServidorConselhoPeloId(String sigla, Integer matricula, Short vinculo) {
		VRapServidorConselho vRapServidorConselho = this.registroColaboradorFacade.obterVRapServidorConselhoPeloId(matricula, vinculo, sigla);
		RapServidorConselhoVO rapServidorConselhoVO = null;
		if (vRapServidorConselho != null) {
			rapServidorConselhoVO = new RapServidorConselhoVO();
			rapServidorConselhoVO.setNroRegConselho(vRapServidorConselho.getNroRegConselho());
			rapServidorConselhoVO.setNome(vRapServidorConselho.getNome());
			rapServidorConselhoVO.setSigla(vRapServidorConselho.getSigla());
			rapServidorConselhoVO.setSituacao(vRapServidorConselho.getIndSituacao());
			rapServidorConselhoVO.setDtInicioVinculo(vRapServidorConselho.getDtInicioVinculo());
			rapServidorConselhoVO.setDtFimVinculo(vRapServidorConselho.getDtFimVinculo());
			rapServidorConselhoVO.setVinCodigo(vRapServidorConselho.getId().getVinCodigo());
			rapServidorConselhoVO.setMatricula(vRapServidorConselho.getId().getMatricula());
		}
		return rapServidorConselhoVO;
	}
	
	@Override
	public Boolean existeServidorCategoriaProfMedico(Integer matricula, Short vinculo) throws ServiceBusinessException, ServiceException {
		try {
			return this.registroColaboradorFacade.existeServidorCategoriaProfMedico(matricula, vinculo);
		} catch (ApplicationBusinessException e) {
			throw new ServiceBusinessException(messagesUtils.getResourceBundleValue(e));
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public boolean verificarProfissionalPossuiCBOAnestesista(Short vinCodigo, Integer matricula, 
			String[] siglasConselhos, String[] codigosTipoInformacoes) throws ServiceException {
		try {
			return this.registroColaboradorFacade.verificarProfissionalPossuiCBOAnestesista(vinCodigo, matricula, siglasConselhos, codigosTipoInformacoes);
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}
}
