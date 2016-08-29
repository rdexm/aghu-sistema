package br.gov.mec.aghu.procedimentoterapeutico.business;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MptFavoritoServidor;
import br.gov.mec.aghu.model.MptSalas;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptFavoritoServidorDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptSalasDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptTipoSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ManterFavoritosUsuarioVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MptFavoritoServidorRN extends BaseBusiness {


	private static final long serialVersionUID = 7238928475166395719L;
	
	@Inject
	private MptTipoSessaoDAO mptTipoSessaoDAO;
	
	@Inject
	private MptSalasDAO mptSalasDAO;
	
	@Inject
	private MptFavoritoServidorDAO mptFavoritoServidorDAO;
	
	private static final Log LOG = LogFactory
			.getLog(MptFavoritoServidorRN.class);

	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	private enum MptFavoritoServidorRNRNExceptionCode implements
	BusinessExceptionCode {
		MSG_SELECIONE_TIPO_SESSAO, MSG_POSSUI_FAVORITO;
	}
	
	public void persistirMptFavoritoServidor(ManterFavoritosUsuarioVO favoritoVO, RapServidores servidor) {
		if (favoritoVO != null) {
			MptFavoritoServidor favorito = new MptFavoritoServidor();
			
			if(favoritoVO.getSalas() != null && favoritoVO.getSalas().getSeq() != null){
				MptSalas originalSalas = mptSalasDAO.obterPorChavePrimaria(favoritoVO.getSalas().getSeq());
				favorito.setSala(originalSalas);
			}
			
			MptTipoSessao originalTipoSessao = mptTipoSessaoDAO.obterPorChavePrimaria(favoritoVO.getSessao().getSeq());
			
			favorito.setTipoSessao(originalTipoSessao);
			favorito.setServidor(servidor);
			favorito.setCriadoEm(new Date());
			this.mptFavoritoServidorDAO.persistir(favorito);
		}
	}
	
	public void removerMptFavoritoServidor(Integer favoritoSeq) throws BaseException{
			final MptFavoritoServidor favoritoServidor = this.mptFavoritoServidorDAO.obterPorChavePrimaria(favoritoSeq);
			this.mptFavoritoServidorDAO.remover(favoritoServidor);
	}
	
	public ManterFavoritosUsuarioVO adicionarFavorito (MptSalas sala,  MptTipoSessao tipoSessao, ManterFavoritosUsuarioVO favorito) throws BaseListException {
		BaseListException lista = new BaseListException();
		if(tipoSessao == null){
			if(tipoSessao == null){
				lista.add(new ApplicationBusinessException(MptFavoritoServidorRNRNExceptionCode.MSG_SELECIONE_TIPO_SESSAO));
			}
		}else{
			if(favorito != null){
				lista.add(new ApplicationBusinessException(MptFavoritoServidorRNRNExceptionCode.MSG_POSSUI_FAVORITO));
			}else{
				favorito = new ManterFavoritosUsuarioVO();
				favorito.setSessao(tipoSessao);
				favorito.setTpsDescricao(tipoSessao.getDescricao());
				if(sala != null){
					favorito.setSalas(sala);
					favorito.setSalasDescricao(sala.getDescricao());
				}
			}
		}
		if (lista.hasException()) {
			throw lista;
		}
		return favorito;
	}

}
