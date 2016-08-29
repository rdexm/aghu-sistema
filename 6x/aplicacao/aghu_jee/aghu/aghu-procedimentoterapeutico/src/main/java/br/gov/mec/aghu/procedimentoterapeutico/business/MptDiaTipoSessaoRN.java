package br.gov.mec.aghu.procedimentoterapeutico.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MptDiaTipoSessao;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptDiaTipoSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptTipoSessaoDAO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class MptDiaTipoSessaoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MptDiaTipoSessaoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private MptTipoSessaoDAO mptTipoSessaoDAO;
	
	@Inject
	private MptDiaTipoSessaoDAO mptDiaTipoSessaoDAO;

	private static final long serialVersionUID = 2797194818319163103L;
	
	public void validarMptDiaTipoSessao(Boolean segunda, Boolean terca, Boolean quarta, Boolean quinta, Boolean sexta,
			Boolean sabado, Boolean domingo, Short tpsSeq) throws ApplicationBusinessException {
		 
		if (segunda.equals(Boolean.TRUE)) {
			this.inserirMptDiaTipoSessao((byte) 2, tpsSeq);
		} else {
			excluirDiaTipoSessaoEspecifico((byte) 2, tpsSeq);
		}
		
		if (terca.equals(Boolean.TRUE)) {
			this.inserirMptDiaTipoSessao((byte) 3, tpsSeq);
		} else {
			excluirDiaTipoSessaoEspecifico((byte) 3, tpsSeq);
		}
		
		if (quarta.equals(Boolean.TRUE)) {
			this.inserirMptDiaTipoSessao((byte) 4, tpsSeq);
		} else {
			excluirDiaTipoSessaoEspecifico((byte) 4, tpsSeq);
		}
		
		if (quinta.equals(Boolean.TRUE)) {
			this.inserirMptDiaTipoSessao((byte) 5, tpsSeq);
		} else {
			excluirDiaTipoSessaoEspecifico((byte) 5, tpsSeq);
		}
		
		if (sexta.equals(Boolean.TRUE)) {
			this.inserirMptDiaTipoSessao((byte) 6, tpsSeq);
		} else {
			excluirDiaTipoSessaoEspecifico((byte) 6, tpsSeq);
		}
		
		if (sabado.equals(Boolean.TRUE)) {
			this.inserirMptDiaTipoSessao((byte) 7, tpsSeq);
		} else {
			excluirDiaTipoSessaoEspecifico((byte) 7, tpsSeq);
		}
		
		if (domingo.equals(Boolean.TRUE)) {
			this.inserirMptDiaTipoSessao((byte) 1, tpsSeq);
		} else {
			excluirDiaTipoSessaoEspecifico((byte) 1, tpsSeq);
		}
	}
	
	public void inserirMptDiaTipoSessao(Byte dia, Short tpsSeq) throws ApplicationBusinessException {
		if (!getMptDiaTipoSessaoDAO().verificarExistenciaDiaTipoSessaoPorDiaTpsSeq(dia, tpsSeq)) {
			RapServidores servidorLogado = getRegistroColaboradorFacade().obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
			
			MptTipoSessao tipoSessao = getMptTipoSessaoDAO().obterPorChavePrimaria(tpsSeq);
			
			MptDiaTipoSessao mptDiaTipoSessao = new MptDiaTipoSessao();
			mptDiaTipoSessao.setTipoSessao(tipoSessao);
			mptDiaTipoSessao.setServidor(servidorLogado);
			mptDiaTipoSessao.setCriadoEm(new Date());
			mptDiaTipoSessao.setDia(dia);
			
			getMptDiaTipoSessaoDAO().persistir(mptDiaTipoSessao);
		}
	}
	
	public void excluirDiaTipoSessaoEspecifico(Byte dia, Short tpsSeq) {
		MptDiaTipoSessao mptDiaTipoSessao = getMptDiaTipoSessaoDAO().obterDiaTipoSessaoPorDiaTpsSeq(dia, tpsSeq);
		if (mptDiaTipoSessao != null) {
			this.getMptDiaTipoSessaoDAO().remover(mptDiaTipoSessao);
		}
	}
	
	public void excluirMptDiaTipoSessao(Short tpsSeq) {
		List<MptDiaTipoSessao> listaDias = this.getMptDiaTipoSessaoDAO().obterDiasPorTipoSessao(tpsSeq);
		
		for (MptDiaTipoSessao item : listaDias) {
			this.getMptDiaTipoSessaoDAO().remover(item);
		}
	}
	
	public IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}

	public MptTipoSessaoDAO getMptTipoSessaoDAO() {
		return mptTipoSessaoDAO;
	}

	public MptDiaTipoSessaoDAO getMptDiaTipoSessaoDAO() {
		return mptDiaTipoSessaoDAO;
	}
}
