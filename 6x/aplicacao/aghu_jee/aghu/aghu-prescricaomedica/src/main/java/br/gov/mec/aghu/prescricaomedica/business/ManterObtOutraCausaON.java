package br.gov.mec.aghu.prescricaomedica.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmObtOutraCausa;
import br.gov.mec.aghu.model.MpmObtOutraCausaId;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmObtOutraCausaDAO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaDiagnosticosCidVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * @author lalegre
 *
 */
@Stateless
public class ManterObtOutraCausaON extends BaseBusiness {


@EJB
private ManterObtOutraCausaRN manterObtOutraCausaRN;

private static final Log LOG = LogFactory.getLog(ManterObtOutraCausaON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmAltaSumarioDAO mpmAltaSumarioDAO;

@Inject
private MpmObtOutraCausaDAO mpmObtOutraCausaDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -170519655049969869L;

	public enum ManterObtOutraCausaONExceptionCode implements BusinessExceptionCode {
		ERRO_ALTERAR_REGISTRO_OBITO_OUTRA_CAUSA;

		public void throwException(Object... params)
		throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}

	}

	/**
	 * Cria uma cópia de ObtOutraCausa
	 * @param altaSumario
	 * @param antigoAsuSeqp
	 */
	public void versionarObtOutraCausa(MpmAltaSumario altaSumario, Short antigoAsuSeqp) throws ApplicationBusinessException {
		
		if(altaSumario!=null){
			
			List<MpmObtOutraCausa> listaObtOutraCausa = this.getMpmObtOutraCausaDAO().obterMpmObtOutraCausa(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), antigoAsuSeqp);
			
			for (MpmObtOutraCausa obtOutraCausa : listaObtOutraCausa) {
				
				MpmObtOutraCausa novoObtOutraCausa = new MpmObtOutraCausa();
				novoObtOutraCausa.setMpmAltaSumarios(altaSumario);
				novoObtOutraCausa.setCid(obtOutraCausa.getCid());
				novoObtOutraCausa.setComplCid(obtOutraCausa.getComplCid());
				novoObtOutraCausa.setDescCid(obtOutraCausa.getDescCid());
				novoObtOutraCausa.setDiagnostico(obtOutraCausa.getDiagnostico());
				novoObtOutraCausa.setIndCarga(obtOutraCausa.getIndCarga());
				novoObtOutraCausa.setIndSituacao(obtOutraCausa.getIndSituacao());
				novoObtOutraCausa.setMpmCidAtendimentos(obtOutraCausa.getMpmCidAtendimentos());
				this.getManterObtOutraCausaRN().inserirObtOutraCausa(novoObtOutraCausa);
				
			}
		}
	
	}
	
	/**
	 * Remove ObtOutraCausa
	 * @param altaSumario
	 */
	public void removerObtOutraCausa(MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		if (altaSumario!=null) {
			List<MpmObtOutraCausa> listaObtOutraCausa = this.getMpmObtOutraCausaDAO().obterMpmObtOutraCausa(
					altaSumario.getId().getApaAtdSeq(), 
					altaSumario.getId().getApaSeq(), 
					altaSumario.getId().getSeqp(),
					false
			);
			
			for (MpmObtOutraCausa obtOutraCausa : listaObtOutraCausa) {
				this.getManterObtOutraCausaRN().removerObtOutraCausa(obtOutraCausa);
			}
		}
	}
	
	public void removerObtOutraCausa(SumarioAltaDiagnosticosCidVO itemGrid) throws ApplicationBusinessException{	
		MpmObtOutraCausa mpmObtOutraCausa = this.getMpmObtOutraCausaDAO().obterPorChavePrimaria(itemGrid.getObtOutraCausa().getId());
		this.getManterObtOutraCausaRN().removerObtOutraCausa(mpmObtOutraCausa);
	}

	
	public String persistirOutraCausa(
			SumarioAltaDiagnosticosCidVO itemSelecionado) throws ApplicationBusinessException {		
		if(itemSelecionado.getObtOutraCausa() != null && itemSelecionado.getObtOutraCausa().getId() != null){
			preAtualizarOutraCausa(itemSelecionado);
			return "MENSAGEM_SUCESSO_ALTERACAO_OUTRA_CAUSA_MORTE";
		}else{
			preInserirOutraCausa(itemSelecionado);
			return "MENSAGEM_SUCESSO_INCLUSAO_OUTRA_CAUSA_MORTE";
			
		}
		
	}
	
	public void preAtualizarOutraCausa(SumarioAltaDiagnosticosCidVO itemSelecionado) throws ApplicationBusinessException{
		
		MpmObtOutraCausa mpmObtOutraCausaEdicao = this.getMpmObtOutraCausaDAO().obterPorChavePrimaria(itemSelecionado.getObtOutraCausa().getId()); 
		
		if(mpmObtOutraCausaEdicao.getIndCarga().equals(DominioSimNao.S)){
			ManterObtOutraCausaONExceptionCode.ERRO_ALTERAR_REGISTRO_OBITO_OUTRA_CAUSA.throwException(); 
		}else{
			mpmObtOutraCausaEdicao.setComplCid(itemSelecionado.getComplementoEditado());
			mpmObtOutraCausaEdicao.setCid(itemSelecionado.getCid());
			this.getManterObtOutraCausaRN().atualizarObtOutraCausa(mpmObtOutraCausaEdicao);
		}
	}
	
	public void preInserirOutraCausa(SumarioAltaDiagnosticosCidVO obtOutraCausa) throws ApplicationBusinessException {		
		MpmAltaSumario altaSumario = this.getMpmAltaSumarioDAO().obterAltaSumarioPeloId(obtOutraCausa.getId().getApaAtdSeq(),obtOutraCausa.getId().getApaSeq(),obtOutraCausa.getId().getSeqp());
		if(altaSumario != null){
			MpmObtOutraCausa mpmObtOutraCausaNovo = new MpmObtOutraCausa();
			MpmObtOutraCausaId id = new MpmObtOutraCausaId();
			id.setAsuApaAtdSeq(altaSumario.getId().getApaAtdSeq());
			id.setAsuApaSeq(altaSumario.getId().getApaSeq());
			id.setAsuSeqp(altaSumario.getId().getSeqp());
			
			mpmObtOutraCausaNovo.setMpmAltaSumarios(altaSumario);
			
			this.getMpmObtOutraCausaDAO().obterValorSequencialId(mpmObtOutraCausaNovo);

			mpmObtOutraCausaNovo.setId(id);
			
			mpmObtOutraCausaNovo.setIndSituacao(DominioSituacao.A);

			if(obtOutraCausa.getCiaSeq() != null){
				mpmObtOutraCausaNovo.setIndCarga(DominioSimNao.S);
			}else{
				mpmObtOutraCausaNovo.setIndCarga(DominioSimNao.N);
			}

			mpmObtOutraCausaNovo.setDescCid(ManterAltaSumarioON.montaDescricaoCIDComPrimeiraMaiuscula(obtOutraCausa.getCid().getDescricao(), obtOutraCausa.getCid().getCodigo()));		
			mpmObtOutraCausaNovo.setCid(obtOutraCausa.getCid());
			mpmObtOutraCausaNovo.setComplCid(obtOutraCausa.getComplementoEditado());
			
			
			this.getManterObtOutraCausaRN().inserirObtOutraCausa(mpmObtOutraCausaNovo);
		}
		
	}
	
	
	protected MpmObtOutraCausaDAO getMpmObtOutraCausaDAO() {
		return mpmObtOutraCausaDAO;
	}
	
	protected ManterObtOutraCausaRN getManterObtOutraCausaRN() {
		return manterObtOutraCausaRN;
	}
	
	protected MpmAltaSumarioDAO getMpmAltaSumarioDAO(){
		return mpmAltaSumarioDAO;
	}
	
}
