package br.gov.mec.aghu.perinatologia.business;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.model.McoExameExterno;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.perinatologia.dao.McoExameExternoDAO;
import br.gov.mec.aghu.perinatologia.dao.McoResultadoExameSignifsDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class McoExameExternoRN extends BaseBusiness {
	private static final long serialVersionUID = 3689855163000807219L;

	@Inject
	McoExameExternoDAO mcoExameExternoDAO;
	
	@Inject
	McoResultadoExameSignifsDAO mcoResultadoExameSignifsDAO;
	
	@Inject
	private RapServidoresDAO servidorDAO;

	@Inject
	@QualificadorUsuario
	private Usuario usuario;
	
	private enum McoExameExternoRNExceptionCode implements BusinessExceptionCode {
		MSG_EXAME_EXAME_EXTERNO,
		MSG_DUPLICADO_EXAME_EXTERNO,
		MCO_00571,
		MSG_EXCLUI_EXAME_EXTERNO,
		;
	}
	
	private void alterar(McoExameExterno exameExterno, McoExameExterno originalMcoExameExterno) throws ApplicationBusinessException {
	
		if(CoreUtil.modificados(originalMcoExameExterno.getDescricao(), exameExterno.getDescricao())){
			throw new ApplicationBusinessException(McoExameExternoRNExceptionCode.MCO_00571);
		}
		
		mcoExameExternoDAO.atualizar(exameExterno);
	}

	private void incluir(McoExameExterno exameExterno)throws ApplicationBusinessException {
//		AelExames aelExamesPossui = aelExamesDAO.pesquisarAelExamesExternos(nome);
//		
//		if(aelExamesPossui != null) {
//			throw new ApplicationBusinessException(McoExameExternoRNExceptionCode.MSG_EXAME_EXAME_EXTERNO);
//		}
		McoExameExterno exameExternoPossui = mcoExameExternoDAO.pesquisarExamesExternos(exameExterno.getDescricao());
		if(exameExternoPossui != null) {
			throw new ApplicationBusinessException(McoExameExternoRNExceptionCode.MSG_DUPLICADO_EXAME_EXTERNO);
		}
		
		exameExterno.setCriadoEm(new Date());
		
		exameExterno.setRapServidores(servidorDAO.obter(new RapServidoresId(usuario.getMatricula(),usuario.getVinculo())));
		
		mcoExameExternoDAO.persistir(exameExterno);
		
	}
	
	private void excluir(McoExameExterno exameExterno) throws ApplicationBusinessException{
		if(!mcoResultadoExameSignifsDAO.pesquisarExameExternoUtilizado(exameExterno.getSeq())){
			mcoExameExternoDAO.removerPorId(exameExterno.getSeq());
		}
		else {
			throw new ApplicationBusinessException(McoExameExternoRNExceptionCode.MSG_EXCLUI_EXAME_EXTERNO);
		}
	}
	
	public void persistirExameExterno(McoExameExterno exameExterno) throws ApplicationBusinessException{
		if(exameExterno.getSeq() != null){
			McoExameExterno originalMcoExameExterno = mcoExameExternoDAO.obterOriginal(exameExterno);
			this.alterar(exameExterno, originalMcoExameExterno);
		}
		else {
			this.incluir(exameExterno);
		}
	}
	
	public void ativarInativarExameExterno(McoExameExterno exameExterno) throws ApplicationBusinessException {
		McoExameExterno originalMcoExameExterno = mcoExameExternoDAO.obterOriginal(exameExterno);
		exameExterno.setIndSituacao(exameExterno.getIndSituacao().isAtivo() ? DominioSituacao.I : DominioSituacao.A);
		this.alterar(exameExterno, originalMcoExameExterno);
	}
	
	public void excluirExameExterno(McoExameExterno exameExterno) throws ApplicationBusinessException {
		this.excluir(exameExterno);
	}
	
	@Override
	protected Log getLogger() {
		// TODO Auto-generated method stub
		return null;
	}

}
