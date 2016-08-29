package br.gov.mec.aghu.patrimonio.business;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.casca.dao.PerfilDAO;
import br.gov.mec.aghu.casca.dao.PerfisUsuariosDAO;
import br.gov.mec.aghu.casca.dao.UsuarioDAO;
import br.gov.mec.aghu.casca.model.Perfil;
import br.gov.mec.aghu.casca.model.PerfisUsuarios;
import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.model.PtmServAreaTecAvaliacao;
import br.gov.mec.aghu.model.PtmServAreaTecAvaliacaoId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.patrimonio.dao.PtmServAreaTecAvaliacaoDAO;
import br.gov.mec.aghu.patrimonio.vo.UsuarioTecnicoVO;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;


@Stateless
public class PtmServAreaTecnicaRN extends BaseBusiness implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 887456L;
	
	private static final Log LOG = LogFactory.getLog(PtmServAreaTecnicaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private PtmServAreaTecAvaliacaoDAO ptmServAreaTecAvaliacaoDAO;
	
	@Inject 
	private PerfisUsuariosDAO perfisUsuariosDAO;
	
	@Inject
	private UsuarioDAO usuarioDAO;
	
	@Inject
	private PerfilDAO perfilDAO;
	
	@Inject
	private RapServidoresDAO rapServidoresDAO;
	
	public enum PatrimonioRNExceptionCode implements BusinessExceptionCode {
		USUARIO_TECNICO_JA_ASSOCIADO, USUARIO_TECNICO_PADRAO
	}	
	
	public void validarAssociacaoDeTecnicoAreaAvaliacao(List <UsuarioTecnicoVO> tecnicosList, RapServidores tecnicoSB1, Integer seqAreaTec) throws ApplicationBusinessException {
		for (UsuarioTecnicoVO vo : tecnicosList) {
			if(tecnicoSB1.getId().getMatricula().equals(vo.getMatRapTecnico()) && tecnicoSB1.getId().getVinCodigo().equals(vo.getSerVinCodigoTecnico()) && seqAreaTec == vo.getSeqAreaTecAvaliacao()){
				throw new ApplicationBusinessException(PatrimonioRNExceptionCode.USUARIO_TECNICO_JA_ASSOCIADO); 
			}
		}
	}
	//#43482 RN03
	public void validarUnicidadeTecnicoPadrao(List <UsuarioTecnicoVO> tecnicosList) throws ApplicationBusinessException {
		for (UsuarioTecnicoVO usuarioTecnicoVO : tecnicosList) {
			if(usuarioTecnicoVO.getTecnicoPadrao()){
				throw new ApplicationBusinessException(PatrimonioRNExceptionCode.USUARIO_TECNICO_PADRAO);
			}
		}
	}
	//#43482 
	public void gravarAlteracoesAssociarTecnicoPadrao(List <UsuarioTecnicoVO> tecnicosExcluidos, List <UsuarioTecnicoVO> tecnicosAdicionados){
		//RN02
		if(tecnicosExcluidos != null && !tecnicosExcluidos.isEmpty()){
			for (UsuarioTecnicoVO vo : tecnicosExcluidos) {
				PtmServAreaTecAvaliacaoId id = new PtmServAreaTecAvaliacaoId(vo.getSeqAreaTecAvaliacao(), vo.getMatRapTecnico(), vo.getSerVinCodigoTecnico());
				if(ptmServAreaTecAvaliacaoDAO.obterPorChavePrimaria(id) != null){
					PtmServAreaTecAvaliacao tecnico = ptmServAreaTecAvaliacaoDAO.obterPorChavePrimaria(id);
					removerPerfilUsuario(vo);
					ptmServAreaTecAvaliacaoDAO.remover(tecnico);
				}
			}
			ptmServAreaTecAvaliacaoDAO.flush();
		}
		//RN04 Edita ou adiciona um tecnico 
		if(tecnicosAdicionados != null && !tecnicosAdicionados.isEmpty()){
			for(UsuarioTecnicoVO vo : tecnicosAdicionados){
				PtmServAreaTecAvaliacaoId id = new PtmServAreaTecAvaliacaoId(vo.getSeqAreaTecAvaliacao(), vo.getMatRapTecnico(), vo.getSerVinCodigoTecnico());
				PtmServAreaTecAvaliacao tecnico = null;
				tecnico = ptmServAreaTecAvaliacaoDAO.obterPorChavePrimaria(id);

				if(tecnico == null){
					tecnico = new PtmServAreaTecAvaliacao();
					tecnico.setId(id);
					tecnico.setTecnicoPadrao(vo.getTecnicoPadrao());
					tecnico.setServidorCriacao(rapServidoresDAO.obter(new RapServidoresId(vo.getMatRapCriacao(), vo.getSerVinCodigoCriacao())));
					ptmServAreaTecAvaliacaoDAO.persistir(tecnico);
					ptmServAreaTecAvaliacaoDAO.flush();
					adcionarPerfilUsuario(vo);
					perfisUsuariosDAO.flush();
				}else{
					tecnico.setTecnicoPadrao(vo.getTecnicoPadrao());
					ptmServAreaTecAvaliacaoDAO.atualizar(tecnico);
					perfisUsuariosDAO.flush();
				}
			}
		}
	}
	
	
	//#43482 RN07
	private void adcionarPerfilUsuario(UsuarioTecnicoVO vo){
		RapServidoresId id = new RapServidoresId(vo.getMatRapTecnico(), vo.getSerVinCodigoTecnico());
		RapServidores servidor = rapServidoresDAO.obterServidor(id);
		if(servidor.getUsuario() != null){
			Usuario usuario = usuarioDAO.recuperarUsuario(servidor.getUsuario());
			Perfil perfil = perfilDAO.pesquisarPerfil("ADM69");
			if(usuario != null && perfil != null){
				if(perfisUsuariosDAO.pesquisarPerfisUsuariosPorUsuarioPerfil(usuario, perfil) == null){
					PerfisUsuarios perfilUsuario = new PerfisUsuarios();
					perfilUsuario.setUsuario(usuario);
					perfilUsuario.setPerfil(perfil);
					perfilUsuario.setDataCriacao(new Date());
					perfisUsuariosDAO.persistir(perfilUsuario);
				}
			}
		}
	}
	
	//#43482 RN08
	private void removerPerfilUsuario(UsuarioTecnicoVO vo){
		RapServidoresId id = new RapServidoresId(vo.getMatRapTecnico(), vo.getSerVinCodigoTecnico());
		Usuario usuario = usuarioDAO.recuperarUsuario(rapServidoresDAO.obterServidor(id).getUsuario());
		PerfisUsuarios perfilUsuario = perfisUsuariosDAO.obterPerfilUsuarioLogin(usuario.getLogin(), "ADM69");
		if(perfilUsuario != null){
			perfisUsuariosDAO.remover(perfilUsuario);
			perfisUsuariosDAO.flush();
		}
	}	
	
}
