package br.gov.mec.aghu.procedimentoterapeutico.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MptTipoIntercorrencia;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.model.MptTipoSessaoTipoIntercor;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptTipoSessaoTipoIntercorDAO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

@Stateless
public class MptTipoSessaoTipoIntercorRN extends BaseBusiness {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7562570421136920305L;
	
	protected enum  MptTipoSessaoTipoIntercorRNExceptionCode implements
	BusinessExceptionCode {
		MSG_VINCULO_EXISTE, MSG_ERRO_INCLUSAO_INTERCOR;
	}
	
	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;
	
	@Inject
	private MptTipoSessaoTipoIntercorDAO mptTipoSessaoTipoIntercorDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	private static final Log LOG = LogFactory.getLog(MptTipoSessaoTipoIntercorRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	public boolean adicionarVinculoIntercorrrenciaTipoSessao(MptTipoIntercorrencia tipoIntercorrencia, MptTipoSessao tipoSessao) throws ApplicationBusinessException{
		boolean gravou = false;
		if( tipoIntercorrencia.getSeq() != null && tipoSessao.getSeq() !=  null){
			Boolean vinculo = verificarVinculo(tipoIntercorrencia.getSeq(), tipoSessao.getSeq());
			if(vinculo){
				throw new ApplicationBusinessException(MptTipoSessaoTipoIntercorRNExceptionCode.MSG_VINCULO_EXISTE, Severity.WARN);
			}else{
				if(vinculo == false && vinculo != null){
					RapServidores servidor = new RapServidores();
					
					MptTipoSessaoTipoIntercor tipoSessaoTipoIntercorrencia = new MptTipoSessaoTipoIntercor();
				
					servidor = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
					
					tipoSessaoTipoIntercorrencia.setServidor(servidor);
					tipoSessaoTipoIntercorrencia.setCriadoEm(new Date());
					tipoSessaoTipoIntercorrencia.setTpsSeq(tipoSessao.getSeq());
					tipoSessaoTipoIntercorrencia.setTpiSeq(tipoIntercorrencia.getSeq());
					
					mptTipoSessaoTipoIntercorDAO.persistir(tipoSessaoTipoIntercorrencia);
					gravou=true;
				}else{
					throw new ApplicationBusinessException(MptTipoSessaoTipoIntercorRNExceptionCode.MSG_ERRO_INCLUSAO_INTERCOR, Severity.ERROR);
				}
			}
		}
		return gravou;
	}
	
	public boolean excluirVinculoIntercorrrenciaTipoSessao(Short seq){
		boolean gravou = false;
		if(seq != null){
			MptTipoSessaoTipoIntercor original = mptTipoSessaoTipoIntercorDAO.obterPorChavePrimaria(seq);
			mptTipoSessaoTipoIntercorDAO.remover(original);
			gravou = true;
		}
		return gravou;
	}
	
	public Boolean verificarVinculo(Short seqTipoIntercorrencia, Short seqTipoSessao){
		if(seqTipoIntercorrencia != null && seqTipoSessao != null){
			Long possuiVinculo = null;
			possuiVinculo = procedimentoTerapeuticoFacade.verificarExistenciaVinculoIntercorrenciaTipoSessao(seqTipoIntercorrencia, seqTipoSessao);
			if(possuiVinculo > 0){
				return true;
			}else{
				if(possuiVinculo == 0){
					return false;
				}
			}
		}
		return null;
	}

}
